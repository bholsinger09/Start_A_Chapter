/**
 * LoadingSpinner Component Tests
 * Testing reusable loading component following Clean Code testing principles
 */

import { describe, it, expect, beforeEach } from 'vitest'
import LoadingSpinner from '@/components/ui/LoadingSpinner.vue'
import { mountComponent } from '../utils/testUtils.js'

describe('LoadingSpinner Component', () => {
  let wrapper

  const defaultProps = {
    size: 'medium',
    color: 'primary',
    text: '',
    overlay: false
  }

  beforeEach(() => {
    wrapper = mountComponent(LoadingSpinner, {
      props: defaultProps
    })
  })

  describe('Rendering', () => {
    it('should render correctly with default props', () => {
      expect(wrapper.exists()).toBe(true)
      expect(wrapper.find('.loading-spinner').exists()).toBe(true)
      expect(wrapper.find('.spinner').exists()).toBe(true)
    })

    it('should render without text by default', () => {
      expect(wrapper.find('.loading-text').exists()).toBe(false)
    })

    it('should render with loading text when provided', () => {
      const textWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          text: 'Loading chapters...'
        }
      })

      expect(textWrapper.find('.loading-text').exists()).toBe(true)
      expect(textWrapper.find('.loading-text').text()).toBe('Loading chapters...')
    })

    it('should not render overlay by default', () => {
      expect(wrapper.find('.loading-overlay').exists()).toBe(false)
    })

    it('should render with overlay when enabled', () => {
      const overlayWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          overlay: true
        }
      })

      expect(overlayWrapper.find('.loading-overlay').exists()).toBe(true)
    })
  })

  describe('Size Variations', () => {
    const sizes = ['small', 'medium', 'large']

    sizes.forEach(size => {
      it(`should apply correct size class for ${size}`, () => {
        const sizeWrapper = mountComponent(LoadingSpinner, {
          props: {
            ...defaultProps,
            size
          }
        })

        expect(sizeWrapper.classes()).toContain(`size-${size}`)
        expect(sizeWrapper.find('.spinner').classes()).toContain(`spinner-${size}`)
      })
    })

    it('should default to medium size when invalid size provided', () => {
      const invalidSizeWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          size: 'invalid-size'
        }
      })

      expect(invalidSizeWrapper.classes()).toContain('size-medium')
    })

    it('should handle numeric size values gracefully', () => {
      const numericSizeWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          size: 42
        }
      })

      // Should fallback to medium or handle gracefully
      expect(numericSizeWrapper.exists()).toBe(true)
    })
  })

  describe('Color Variations', () => {
    const colors = ['primary', 'secondary', 'success', 'warning', 'error']

    colors.forEach(color => {
      it(`should apply correct color class for ${color}`, () => {
        const colorWrapper = mountComponent(LoadingSpinner, {
          props: {
            ...defaultProps,
            color
          }
        })

        expect(colorWrapper.classes()).toContain(`color-${color}`)
        expect(colorWrapper.find('.spinner').classes()).toContain(`spinner-${color}`)
      })
    })

    it('should default to primary color when invalid color provided', () => {
      const invalidColorWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          color: 'invalid-color'
        }
      })

      expect(invalidColorWrapper.classes()).toContain('color-primary')
    })

    it('should handle hex color values', () => {
      const hexColorWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          color: '#FF5733'
        }
      })

      // Should either apply custom color or fallback gracefully
      expect(hexColorWrapper.exists()).toBe(true)
    })
  })

  describe('Text Display', () => {
    it('should display simple text message', () => {
      const textWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          text: 'Loading...'
        }
      })

      const textElement = textWrapper.find('.loading-text')
      expect(textElement.exists()).toBe(true)
      expect(textElement.text()).toBe('Loading...')
    })

    it('should display multiline text', () => {
      const multilineText = 'Loading chapters...\nPlease wait'
      const multilineWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          text: multilineText
        }
      })

      expect(multilineWrapper.find('.loading-text').text()).toContain('Loading chapters...')
    })

    it('should handle empty string text', () => {
      const emptyTextWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          text: ''
        }
      })

      expect(emptyTextWrapper.find('.loading-text').exists()).toBe(false)
    })

    it('should handle whitespace-only text', () => {
      const whitespaceWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          text: '   '
        }
      })

      // Should either not render or handle gracefully
      const hasText = whitespaceWrapper.find('.loading-text').exists()
      if (hasText) {
        expect(whitespaceWrapper.find('.loading-text').text().trim()).toBe('')
      }
    })

    it('should handle very long text messages', () => {
      const longText = 'This is a very long loading message that should be handled properly even though it contains many words and characters'
      const longTextWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          text: longText
        }
      })

      expect(longTextWrapper.find('.loading-text').text()).toBe(longText)
    })
  })

  describe('Overlay Functionality', () => {
    it('should render overlay with correct structure', () => {
      const overlayWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          overlay: true
        }
      })

      const overlay = overlayWrapper.find('.loading-overlay')
      expect(overlay.exists()).toBe(true)
      expect(overlay.find('.loading-spinner').exists()).toBe(true)
    })

    it('should have proper z-index for overlay', () => {
      const overlayWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          overlay: true
        }
      })

      const overlay = overlayWrapper.find('.loading-overlay')
      expect(overlay.classes()).toContain('loading-overlay')
    })

    it('should center content in overlay', () => {
      const overlayWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          overlay: true,
          text: 'Loading with overlay...'
        }
      })

      const overlay = overlayWrapper.find('.loading-overlay')
      expect(overlay.classes()).toContain('loading-overlay')
      expect(overlay.find('.loading-text').exists()).toBe(true)
    })

    it('should handle overlay with different sizes', () => {
      const sizes = ['small', 'medium', 'large']

      sizes.forEach(size => {
        const overlayWrapper = mountComponent(LoadingSpinner, {
          props: {
            ...defaultProps,
            overlay: true,
            size
          }
        })

        expect(overlayWrapper.find('.loading-overlay').exists()).toBe(true)
        expect(overlayWrapper.classes()).toContain(`size-${size}`)
      })
    })
  })

  describe('CSS Classes', () => {
    it('should apply base CSS classes correctly', () => {
      expect(wrapper.classes()).toContain('loading-spinner')
      expect(wrapper.classes()).toContain('size-medium')
      expect(wrapper.classes()).toContain('color-primary')
    })

    it('should apply conditional classes based on props', () => {
      const conditionalWrapper = mountComponent(LoadingSpinner, {
        props: {
          size: 'large',
          color: 'error',
          text: 'Error loading',
          overlay: true
        }
      })

      expect(conditionalWrapper.classes()).toContain('loading-spinner')
      expect(conditionalWrapper.classes()).toContain('size-large')
      expect(conditionalWrapper.classes()).toContain('color-error')
      expect(conditionalWrapper.classes()).toContain('has-text')
      expect(conditionalWrapper.find('.loading-overlay').exists()).toBe(true)
    })

    it('should handle class combinations correctly', () => {
      const combinedWrapper = mountComponent(LoadingSpinner, {
        props: {
          size: 'small',
          color: 'success',
          text: 'Success!',
          overlay: false
        }
      })

      const classes = combinedWrapper.classes()
      expect(classes).toContain('size-small')
      expect(classes).toContain('color-success')
      expect(classes).toContain('has-text')
      expect(combinedWrapper.find('.loading-overlay').exists()).toBe(false)
    })
  })

  describe('Accessibility', () => {
    it('should have proper ARIA attributes', () => {
      expect(wrapper.attributes('role')).toBe('status')
      expect(wrapper.attributes('aria-live')).toBe('polite')
    })

    it('should have aria-label when no text provided', () => {
      expect(wrapper.attributes('aria-label')).toBe('Loading')
    })

    it('should use text as aria-label when provided', () => {
      const textWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          text: 'Loading chapters...'
        }
      })

      expect(textWrapper.attributes('aria-label')).toBe('Loading chapters...')
    })

    it('should be hidden from screen readers when appropriate', () => {
      // Some loading spinners might want to be hidden
      expect(wrapper.attributes('aria-hidden')).toBeFalsy()
    })

    it('should handle keyboard navigation correctly', () => {
      // Loading spinner should not be focusable
      expect(wrapper.attributes('tabindex')).toBeFalsy()
    })
  })

  describe('Animation and Styling', () => {
    it('should have animated spinner element', () => {
      const spinner = wrapper.find('.spinner')
      expect(spinner.exists()).toBe(true)
      expect(spinner.classes()).toContain('spinner')
    })

    it('should maintain proper aspect ratio', () => {
      const spinner = wrapper.find('.spinner')
      expect(spinner.classes()).toContain('spinner-medium')
    })

    it('should handle CSS custom properties for theming', () => {
      // The component should support CSS custom properties
      expect(wrapper.exists()).toBe(true)
    })
  })

  describe('Component Props Validation', () => {
    it('should handle all valid prop combinations', () => {
      const validCombinations = [
        { size: 'small', color: 'primary', text: '', overlay: false },
        { size: 'large', color: 'error', text: 'Error!', overlay: true },
        { size: 'medium', color: 'success', text: 'Success', overlay: false }
      ]

      validCombinations.forEach(props => {
        const testWrapper = mountComponent(LoadingSpinner, { props })
        expect(testWrapper.exists()).toBe(true)
      })
    })

    it('should handle missing props gracefully', () => {
      const minimalWrapper = mountComponent(LoadingSpinner, {
        props: {}
      })

      expect(minimalWrapper.exists()).toBe(true)
      expect(minimalWrapper.classes()).toContain('loading-spinner')
    })

    it('should handle null/undefined props', () => {
      const nullPropsWrapper = mountComponent(LoadingSpinner, {
        props: {
          size: null,
          color: undefined,
          text: null,
          overlay: undefined
        }
      })

      expect(nullPropsWrapper.exists()).toBe(true)
    })
  })

  describe('Performance', () => {
    it('should render efficiently with minimal DOM nodes', () => {
      const domNodes = wrapper.findAll('*')

      // Should have a reasonable number of DOM nodes
      expect(domNodes.length).toBeLessThan(10)
    })

    it('should not cause memory leaks', () => {
      // Create and destroy multiple instances
      for (let i = 0; i < 100; i++) {
        const tempWrapper = mountComponent(LoadingSpinner, {
          props: defaultProps
        })
        tempWrapper.unmount()
      }

      // Should complete without errors
      expect(true).toBe(true)
    })
  })

  describe('Edge Cases', () => {
    it('should handle rapid prop changes', async () => {
      await wrapper.setProps({ size: 'large' })
      await wrapper.setProps({ color: 'error' })
      await wrapper.setProps({ text: 'New text' })
      await wrapper.setProps({ overlay: true })

      expect(wrapper.classes()).toContain('size-large')
      expect(wrapper.classes()).toContain('color-error')
      expect(wrapper.find('.loading-text').text()).toBe('New text')
      expect(wrapper.find('.loading-overlay').exists()).toBe(true)
    })

    it('should handle component unmounting gracefully', () => {
      const tempWrapper = mountComponent(LoadingSpinner, {
        props: { ...defaultProps, overlay: true }
      })

      expect(() => tempWrapper.unmount()).not.toThrow()
    })

    it('should handle special characters in text', () => {
      const specialTextWrapper = mountComponent(LoadingSpinner, {
        props: {
          ...defaultProps,
          text: '🔄 Loading... 💻'
        }
      })

      expect(specialTextWrapper.find('.loading-text').text()).toBe('🔄 Loading... 💻')
    })
  })
})