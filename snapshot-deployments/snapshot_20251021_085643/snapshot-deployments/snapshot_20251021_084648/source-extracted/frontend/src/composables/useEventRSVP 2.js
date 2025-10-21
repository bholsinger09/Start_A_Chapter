import { ref, computed, reactive, watch } from 'vue'
import { useEventAPI } from './useEventAPI'

export function useEventRSVP(eventId, currentUserId, initialRsvps = []) {
  // Reactive state
  const rsvps = ref([...initialRsvps])
  const isLoading = ref(false)
  const error = ref(null)

  // Get API functions
  const { createRSVP, updateRSVP: apiUpdateRSVP, deleteRSVP, fetchEventRSVPs } = useEventAPI()

  // Current user's RSVP
  const currentUserRsvp = computed(() => {
    return rsvps.value.find(rsvp => rsvp.userId === currentUserId)
  })

  // RSVP counts by status
  const rsvpCounts = computed(() => {
    const counts = {
      ATTENDING: 0,
      NOT_ATTENDING: 0,
      MAYBE: 0,
      WAITLIST: 0,
      PENDING: 0
    }

    rsvps.value.forEach(rsvp => {
      if (counts.hasOwnProperty(rsvp.status)) {
        counts[rsvp.status]++
      }
    })

    return counts
  })

  // Helper computed properties
  const totalAttending = computed(() => {
    return rsvpCounts.value.ATTENDING + rsvpCounts.value.WAITLIST
  })

  const hasUserRsvped = computed(() => {
    return !!currentUserRsvp.value
  })

  // Methods
  const loadRSVPs = async () => {
    if (!eventId) return

    try {
      isLoading.value = true
      error.value = null

      const response = await fetchEventRSVPs(eventId)
      rsvps.value = response.data || []
    } catch (err) {
      console.error('Failed to load RSVPs:', err)
      error.value = 'Failed to load RSVPs'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const updateRSVP = async (status) => {
    if (!eventId || !currentUserId) {
      throw new Error('Event ID and User ID are required')
    }

    try {
      isLoading.value = true
      error.value = null

      let response

      if (hasUserRsvped.value) {
        // Update existing RSVP
        response = await apiUpdateRSVP(eventId, currentUserId, { status })
      } else {
        // Create new RSVP
        response = await createRSVP(eventId, {
          userId: currentUserId,
          status
        })
      }

      // Update local state
      const updatedRsvp = response.data
      const existingIndex = rsvps.value.findIndex(rsvp => rsvp.userId === currentUserId)

      if (existingIndex >= 0) {
        rsvps.value[existingIndex] = updatedRsvp
      } else {
        rsvps.value.push(updatedRsvp)
      }

      return updatedRsvp

    } catch (err) {
      console.error('Failed to update RSVP:', err)
      error.value = 'Failed to update RSVP'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const removeRSVP = async () => {
    if (!hasUserRsvped.value) return

    try {
      isLoading.value = true
      error.value = null

      await deleteRSVP(eventId, currentUserId)

      // Remove from local state
      const index = rsvps.value.findIndex(rsvp => rsvp.userId === currentUserId)
      if (index >= 0) {
        rsvps.value.splice(index, 1)
      }

    } catch (err) {
      console.error('Failed to remove RSVP:', err)
      error.value = 'Failed to remove RSVP'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const promoteFromWaitlist = async (rsvpId) => {
    try {
      isLoading.value = true
      error.value = null

      const response = await apiUpdateRSVP(eventId, rsvpId, { status: 'ATTENDING' })

      // Update local state
      const index = rsvps.value.findIndex(rsvp => rsvp.id === rsvpId)
      if (index >= 0) {
        rsvps.value[index] = response.data
      }

      return response.data

    } catch (err) {
      console.error('Failed to promote from waitlist:', err)
      error.value = 'Failed to promote from waitlist'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const bulkUpdateRSVPs = async (updates) => {
    try {
      isLoading.value = true
      error.value = null

      // Process updates in parallel
      const promises = updates.map(update =>
        apiUpdateRSVP(eventId, update.userId, { status: update.status })
      )

      const responses = await Promise.all(promises)

      // Update local state
      responses.forEach((response, index) => {
        const updatedRsvp = response.data
        const existingIndex = rsvps.value.findIndex(rsvp => rsvp.userId === updates[index].userId)

        if (existingIndex >= 0) {
          rsvps.value[existingIndex] = updatedRsvp
        }
      })

      return responses.map(r => r.data)

    } catch (err) {
      console.error('Failed to bulk update RSVPs:', err)
      error.value = 'Failed to update RSVPs'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // Utility functions
  const getRSVPsByStatus = (status) => {
    return rsvps.value.filter(rsvp => rsvp.status === status)
  }

  const getWaitlistPosition = (userId) => {
    const waitlistRsvps = getRSVPsByStatus('WAITLIST')
      .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))

    return waitlistRsvps.findIndex(rsvp => rsvp.userId === userId) + 1
  }

  const canUserAttend = (eventCapacity) => {
    if (!eventCapacity) return true // Unlimited capacity

    const attendingCount = rsvpCounts.value.ATTENDING
    return attendingCount < eventCapacity || currentUserRsvp.value?.status === 'ATTENDING'
  }

  const getCapacityInfo = (eventCapacity) => {
    if (!eventCapacity) {
      return {
        isFull: false,
        percentage: 0,
        remaining: Infinity,
        waitlistCount: rsvpCounts.value.WAITLIST
      }
    }

    const attending = rsvpCounts.value.ATTENDING
    const percentage = Math.round((attending / eventCapacity) * 100)

    return {
      isFull: attending >= eventCapacity,
      percentage: Math.min(percentage, 100),
      remaining: Math.max(eventCapacity - attending, 0),
      waitlistCount: rsvpCounts.value.WAITLIST
    }
  }

  // Watch for changes in eventId to reload RSVPs
  watch(
    () => eventId,
    (newEventId) => {
      if (newEventId) {
        loadRSVPs()
      }
    },
    { immediate: true }
  )

  // Return the reactive interface
  return {
    // State
    rsvps,
    isLoading,
    error,

    // Computed
    currentUserRsvp,
    rsvpCounts,
    totalAttending,
    hasUserRsvped,

    // Methods
    loadRSVPs,
    updateRSVP,
    removeRSVP,
    promoteFromWaitlist,
    bulkUpdateRSVPs,

    // Utilities
    getRSVPsByStatus,
    getWaitlistPosition,
    canUserAttend,
    getCapacityInfo
  }
}

// Additional composable for RSVP management (admin/organizer features)
export function useRSVPManagement(eventId) {
  const {
    rsvps,
    isLoading,
    error,
    rsvpCounts,
    loadRSVPs,
    bulkUpdateRSVPs,
    promoteFromWaitlist
  } = useEventRSVP(eventId)

  const selectedRsvps = ref([])
  const bulkAction = ref('')

  const selectedCount = computed(() => selectedRsvps.value.length)

  const toggleSelection = (rsvpId) => {
    const index = selectedRsvps.value.indexOf(rsvpId)
    if (index >= 0) {
      selectedRsvps.value.splice(index, 1)
    } else {
      selectedRsvps.value.push(rsvpId)
    }
  }

  const selectAll = (status = null) => {
    if (status) {
      const filteredRsvps = rsvps.value
        .filter(rsvp => rsvp.status === status)
        .map(rsvp => rsvp.id)
      selectedRsvps.value = filteredRsvps
    } else {
      selectedRsvps.value = rsvps.value.map(rsvp => rsvp.id)
    }
  }

  const clearSelection = () => {
    selectedRsvps.value = []
  }

  const executeBulkAction = async (action) => {
    if (!selectedRsvps.value.length) return

    const updates = selectedRsvps.value.map(rsvpId => {
      const rsvp = rsvps.value.find(r => r.id === rsvpId)
      return {
        userId: rsvp.userId,
        status: action
      }
    })

    await bulkUpdateRSVPs(updates)
    clearSelection()
  }

  const promoteAllWaitlist = async () => {
    const waitlistRsvps = rsvps.value.filter(rsvp => rsvp.status === 'WAITLIST')

    for (const rsvp of waitlistRsvps) {
      await promoteFromWaitlist(rsvp.id)
    }
  }

  return {
    // Extended state
    selectedRsvps,
    bulkAction,
    selectedCount,

    // Inherited from useEventRSVP
    rsvps,
    isLoading,
    error,
    rsvpCounts,
    loadRSVPs,

    // Management methods
    toggleSelection,
    selectAll,
    clearSelection,
    executeBulkAction,
    promoteAllWaitlist,
    promoteFromWaitlist
  }
}