// Composable for chapter link functionality
// Provides consistent URL generation and button behavior across components

export function useChapterLinks() {
  /**
   * Generate appropriate URL based on university location and state
   * @param {Object} chapter - Chapter object with universityName and state
   * @returns {string|null} - URL for chapter resources or null if invalid
   */
  function getCampusLabsUrl(chapter) {
    // Validate input parameters
    if (!chapter || typeof chapter !== 'object') {
      console.warn('getCampusLabsUrl: Invalid chapter object provided')
      return null
    }
    
    if (!chapter.universityName || !chapter.state) {
      return null
    }
    
    try {
      // Special case for Florida - use USF BullsConnect link
      if (chapter.state === 'Florida') {
        return 'https://bullsconnect.usf.edu/tpusa/home/'
      }
      
      // For all other states, create a Google search for Turning Point USA in their area
      const searchQuery = encodeURIComponent(`Turning Point USA ${chapter.state}`)
      return `https://www.google.com/search?q=${searchQuery}`
    } catch (error) {
      console.error('Error generating chapter URL:', error)
      return null
    }
  }
  
  /**
   * Get appropriate button text based on chapter state
   * @param {Object} chapter - Chapter object with state property
   * @returns {string} - Button text to display
   */
  function getButtonText(chapter) {
    if (!chapter || !chapter.state) {
      return 'Find Chapters' // Safe default
    }
    
    if (chapter.state === 'Florida') {
      return 'Chapter Link'
    }
    return 'Find Chapters'
  }
  
  /**
   * Get appropriate tooltip text based on chapter state  
   * @param {Object} chapter - Chapter object with state property
   * @returns {string} - Tooltip text to display
   */
  function getButtonTooltip(chapter) {
    if (!chapter || !chapter.state) {
      return 'Search for Turning Point USA chapters' // Safe default
    }
    
    if (chapter.state === 'Florida') {
      return 'Visit USF BullsConnect - TPUSA Chapter Page'
    }
    return `Search for Turning Point USA chapters in ${chapter.state}`
  }
  
  /**
   * Track link clicks for analytics
   * @param {Object} chapter - Chapter object with universityName and state
   */
  function trackLinkClick(chapter) {
    try {
      if (!chapter) {
        console.warn('trackLinkClick: No chapter provided')
        return
      }
      
      if (chapter.state === 'Florida') {
        console.log(`Florida BullsConnect link clicked for: ${chapter.universityName || 'Unknown University'}`)
      } else {
        console.log(`Google search link clicked for TPUSA in: ${chapter.state || 'Unknown State'}`)
      }
    } catch (error) {
      console.error('Error tracking link click:', error)
    }
  }
  
  return {
    getCampusLabsUrl,
    getButtonText,
    getButtonTooltip,
    trackLinkClick
  }
}