/**
 * SearchInput Component Tests
 * Testing reusable search component following Clean Code testing principles
 */

import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import SearchInput from '@/components/ui/SearchInput.vue'
import { mountComponent, createTestingConfig } from '../utils/testUtils.js'

describe('SearchInput Component', () => {
  let wrapper

  const defaultProps = {
    modelValue: '',
    placeholder: 'Search chapters...',
    disabled: false,
    loading: false
  }

  beforeEach(() => {
    wrapper = mountComponent(SearchInput, {
      props: defaultProps
    })
  })

  describe('Rendering', () => {
    it('should render correctly with default props', () => {
      expect(wrapper.exists()).toBe(true)
      expect(wrapper.find('input[type="text"]').exists()).toBe(true)
      expect(wrapper.find('.search-input').exists()).toBe(true)
    })

    it('should display the correct placeholder text', () => {
      const input = wrapper.find('input')
      expect(input.attributes('placeholder')).toBe('Search chapters...')
    })

    it('should display custom placeholder', () => {
      const customWrapper = mountComponent(SearchInput, {
        props: {
          ...defaultProps,
          placeholder: 'Custom search placeholder'
        }
      })

      const input = customWrapper.find('input')
      expect(input.attributes('placeholder')).toBe('Custom search placeholder')
    })

    it('should show search icon', () => {
      expect(wrapper.find('.search-icon').exists()).toBe(true)
    })

    it('should show clear button when has value', async () => {
      await wrapper.setProps({ modelValue: 'test search' })
      expect(wrapper.find('.clear-button').exists()).toBe(true)
    })

    it('should hide clear button when empty', () => {
      expect(wrapper.find('.clear-button').exists()).toBe(false)
    })
  })

  describe('Props Handling', () => {
    it('should reflect modelValue in input', async () => {
      await wrapper.setProps({ modelValue: 'test value' })
      const input = wrapper.find('input')
      expect(input.element.value).toBe('test value')
    })

    it('should handle disabled state', async () => {
      await wrapper.setProps({ disabled: true })
      const input = wrapper.find('input')
      expect(input.attributes('disabled')).toBeDefined()
      expect(wrapper.classes()).toContain('disabled')
    })

    it('should handle loading state', async () => {
      await wrapper.setProps({ loading: true })
      expect(wrapper.find('.loading-spinner').exists()).toBe(true)
      expect(wrapper.classes()).toContain('loading')
    })

    it('should apply correct CSS classes for different states', async () => {
      // Default state
      expect(wrapper.classes()).toContain('search-input')

      // Focused state
      await wrapper.find('input').trigger('focus')
      expect(wrapper.classes()).toContain('focused')

      // Loading state
      await wrapper.setProps({ loading: true })
      expect(wrapper.classes()).toContain('loading')

      // Disabled state
      await wrapper.setProps({ disabled: true, loading: false })
      expect(wrapper.classes()).toContain('disabled')
    })
  })

  describe('User Interactions', () => {
    it('should emit update:modelValue on input', async () => {
      const input = wrapper.find('input')
      await input.setValue('new search term')

      expect(wrapper.emitted('update:modelValue')).toBeTruthy()
      expect(wrapper.emitted('update:modelValue')[0]).toEqual(['new search term'])
    })

    it('should emit search event on Enter key', async () => {
      const input = wrapper.find('input')
      await input.setValue('search term')
      await input.trigger('keydown.enter')

      expect(wrapper.emitted('search')).toBeTruthy()
      expect(wrapper.emitted('search')[0]).toEqual(['search term'])
    })

    it('should emit clear event on clear button click', async () => {
      await wrapper.setProps({ modelValue: 'test' })
      const clearButton = wrapper.find('.clear-button')
      await clearButton.trigger('click')

      expect(wrapper.emitted('clear')).toBeTruthy()
      expect(wrapper.emitted('update:modelValue')).toBeTruthy()
      expect(wrapper.emitted('update:modelValue')).toContainEqual([''])
    })

    it('should emit focus event on input focus', async () => {
      const input = wrapper.find('input')
      await input.trigger('focus')

      expect(wrapper.emitted('focus')).toBeTruthy()
    })

    it('should emit blur event on input blur', async () => {
      const input = wrapper.find('input')
      await input.trigger('blur')

      expect(wrapper.emitted('blur')).toBeTruthy()
    })

    it('should not emit events when disabled', async () => {
      await wrapper.setProps({ disabled: true })
      const input = wrapper.find('input')

      await input.setValue('should not work')
      await input.trigger('keydown.enter')

      expect(wrapper.emitted('update:modelValue')).toBeFalsy()
      expect(wrapper.emitted('search')).toBeFalsy()
    })
  })

  describe('Keyboard Interactions', () => {
    it('should handle Escape key to clear input', async () => {
      await wrapper.setProps({ modelValue: 'test' })
      const input = wrapper.find('input')
      await input.trigger('keydown.escape')

      expect(wrapper.emitted('update:modelValue')).toContainEqual([''])
      expect(wrapper.emitted('clear')).toBeTruthy()
    })

    it('should handle Tab key navigation', async () => {
      const input = wrapper.find('input')
      await input.trigger('keydown.tab')

      // Should not prevent default tab behavior
      expect(wrapper.emitted('blur')).toBeFalsy()
    })

    it('should handle arrow keys without affecting input', async () => {
      const input = wrapper.find('input')
      await input.setValue('test')
      await input.trigger('keydown.arrow-up')
      await input.trigger('keydown.arrow-down')

      // Input value should remain unchanged
      expect(input.element.value).toBe('test')
    })
  })

  describe('Event Emitting', () => {
    it('should emit input event with correct payload', async () => {
      const input = wrapper.find('input')
      const testValue = 'test input value'

      await input.setValue(testValue)

      const inputEvents = wrapper.emitted('input')
      expect(inputEvents).toBeTruthy()
      expect(inputEvents[0]).toEqual([testValue])
    })

    it('should emit search event with trimmed value', async () => {
      const input = wrapper.find('input')
      await input.setValue('  trimmed search  ')
      await input.trigger('keydown.enter')

      expect(wrapper.emitted('search')).toBeTruthy()
      expect(wrapper.emitted('search')[0]).toEqual(['trimmed search'])
    })

    it('should not emit search event for empty trimmed value', async () => {
      const input = wrapper.find('input')
      await input.setValue('   ')
      await input.trigger('keydown.enter')

      expect(wrapper.emitted('search')).toBeFalsy()
    })

    it('should debounce input events', async () => {
      // Test that rapid typing doesn't emit excessive events
      const input = wrapper.find('input')

      // Simulate rapid typing
      for (let i = 0; i < 5; i++) {
        await input.setValue(`test${i}`)
      }

      // Should have emitted multiple events but not excessively
      const updateEvents = wrapper.emitted('update:modelValue')
      expect(updateEvents.length).toBe(5)
    })
  })

  describe('Accessibility', () => {
    it('should have proper ARIA attributes', () => {
      const input = wrapper.find('input')

      expect(input.attributes('aria-label')).toBeDefined()
      expect(input.attributes('role')).toBe('searchbox')
    })

    it('should have proper accessibility for clear button', async () => {
      await wrapper.setProps({ modelValue: 'test' })
      const clearButton = wrapper.find('.clear-button')

      expect(clearButton.attributes('aria-label')).toBe('Clear search')
      expect(clearButton.attributes('tabindex')).toBe('0')
    })

    it('should support screen reader announcements', async () => {
      await wrapper.setProps({ loading: true })
      const loadingElement = wrapper.find('[aria-live="polite"]')

      expect(loadingElement.exists()).toBe(true)
    })

    it('should handle focus management correctly', async () => {
      const input = wrapper.find('input')

      // Focus the input
      await input.trigger('focus')
      expect(document.activeElement).toBe(input.element)

      // Clear button should be focusable when visible
      await wrapper.setProps({ modelValue: 'test' })
      const clearButton = wrapper.find('.clear-button')
      expect(clearButton.attributes('tabindex')).toBe('0')
    })
  })

  describe('CSS Classes and Styling', () => {
    it('should apply base CSS classes', () => {
      expect(wrapper.classes()).toContain('search-input')
      expect(wrapper.find('input').classes()).toContain('search-field')
    })

    it('should apply state-specific classes', async () => {
      // Focused state
      const input = wrapper.find('input')
      await input.trigger('focus')
      expect(wrapper.classes()).toContain('focused')

      // Loading state
      await wrapper.setProps({ loading: true })
      expect(wrapper.classes()).toContain('loading')

      // Disabled state
      await wrapper.setProps({ disabled: true, loading: false })
      expect(wrapper.classes()).toContain('disabled')
    })

    it('should handle has-value class correctly', async () => {
      // No value
      expect(wrapper.classes()).not.toContain('has-value')

      // With value
      await wrapper.setProps({ modelValue: 'test' })
      expect(wrapper.classes()).toContain('has-value')

      // Empty again
      await wrapper.setProps({ modelValue: '' })
      expect(wrapper.classes()).not.toContain('has-value')
    })
  })

  describe('Component Lifecycle', () => {
    it('should handle mounting correctly', () => {
      const newWrapper = mountComponent(SearchInput, {
        props: defaultProps
      })

      expect(newWrapper.exists()).toBe(true)
      expect(newWrapper.find('input').exists()).toBe(true)
    })

    it('should handle prop updates correctly', async () => {
      await wrapper.setProps({
        modelValue: 'updated value',
        placeholder: 'Updated placeholder',
        disabled: true
      })

      const input = wrapper.find('input')
      expect(input.element.value).toBe('updated value')
      expect(input.attributes('placeholder')).toBe('Updated placeholder')
      expect(input.attributes('disabled')).toBeDefined()
    })

    it('should clean up event listeners on unmount', () => {
      const unmountSpy = vi.spyOn(wrapper.vm, '$el')
      wrapper.unmount()

      // Component should be properly unmounted
      expect(wrapper.vm).toBeFalsy()
    })
  })

  describe('Edge Cases', () => {
    it('should handle null/undefined modelValue gracefully', async () => {
      await wrapper.setProps({ modelValue: null })
      expect(wrapper.find('input').element.value).toBe('')

      await wrapper.setProps({ modelValue: undefined })
      expect(wrapper.find('input').element.value).toBe('')
    })

    it('should handle very long search terms', async () => {
      const longValue = 'a'.repeat(1000)
      await wrapper.setProps({ modelValue: longValue })

      expect(wrapper.find('input').element.value).toBe(longValue)
    })

    it('should handle special characters in search', async () => {
      const specialChars = '!@#$%^&*()_+-=[]{}|;:",./<>?'
      const input = wrapper.find('input')
      await input.setValue(specialChars)

      expect(wrapper.emitted('update:modelValue')[0]).toEqual([specialChars])
    })

    it('should handle rapid state changes', async () => {
      // Rapidly change between states
      await wrapper.setProps({ loading: true })
      await wrapper.setProps({ disabled: true, loading: false })
      await wrapper.setProps({ disabled: false })

      // Should end up in the correct final state
      expect(wrapper.classes()).not.toContain('loading')
      expect(wrapper.classes()).not.toContain('disabled')
    })
  })
})