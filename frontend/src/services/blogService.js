import api from './api.js'

export const blogService = {
    // Blog operations
    async getAllBlogs(page = 0, size = 10) {
        try {
            const response = await api.get(`/blogs?page=${page}&size=${size}`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to fetch blogs'
            }
        }
    },

    async getAllBlogsList() {
        try {
            const response = await api.get('/blogs/list')
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to fetch blogs'
            }
        }
    },

    async getBlogById(id) {
        try {
            const response = await api.get(`/blogs/${id}`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to fetch blog'
            }
        }
    },

    async createBlog(blogData) {
        try {
            const response = await api.post('/blogs', blogData)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to create blog'
            }
        }
    },

    async updateBlog(id, blogData) {
        try {
            const response = await api.put(`/blogs/${id}`, blogData)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to update blog'
            }
        }
    },

    async deleteBlog(id) {
        try {
            const response = await api.delete(`/blogs/${id}`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to delete blog'
            }
        }
    },

    async publishBlog(id) {
        try {
            const response = await api.put(`/blogs/${id}/publish`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to publish blog'
            }
        }
    },

    async unpublishBlog(id) {
        try {
            const response = await api.put(`/blogs/${id}/unpublish`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to unpublish blog'
            }
        }
    },

    async getBlogsByAuthor(authorId, page = 0, size = 10, includeUnpublished = false) {
        try {
            const response = await api.get(`/blogs/author/${authorId}?page=${page}&size=${size}&includeUnpublished=${includeUnpublished}`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to fetch author blogs'
            }
        }
    },

    async searchBlogs(keyword) {
        try {
            const response = await api.get(`/blogs/search?keyword=${encodeURIComponent(keyword)}`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to search blogs'
            }
        }
    },

    async getRecentBlogs(days = 7) {
        try {
            const response = await api.get(`/blogs/recent?days=${days}`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to fetch recent blogs'
            }
        }
    },

    async getPopularBlogs(limit = 10) {
        try {
            const response = await api.get(`/blogs/popular?limit=${limit}`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to fetch popular blogs'
            }
        }
    },

    async getBlogStats() {
        try {
            const response = await api.get('/blogs/stats')
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to fetch blog stats'
            }
        }
    },

    // Comment operations
    async getCommentsByBlog(blogId, page = 0, size = 20) {
        try {
            const response = await api.get(`/comments/blog/${blogId}?page=${page}&size=${size}`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to fetch comments'
            }
        }
    },

    async getCommentsByBlogList(blogId) {
        try {
            const response = await api.get(`/comments/blog/${blogId}/list`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to fetch comments'
            }
        }
    },

    async getCommentsByAuthor(authorId) {
        try {
            const response = await api.get(`/comments/author/${authorId}`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to fetch author comments'
            }
        }
    },

    async createComment(commentData) {
        try {
            const response = await api.post('/comments', commentData)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to create comment'
            }
        }
    },

    async createCommentForBlog(blogId, content, authorId) {
        try {
            const response = await api.post(`/comments/blog/${blogId}`, {
                content,
                authorId
            })
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to create comment'
            }
        }
    },

    async updateComment(id, content) {
        try {
            const response = await api.put(`/comments/${id}`, { content })
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to update comment'
            }
        }
    },

    async deleteComment(id) {
        try {
            const response = await api.delete(`/comments/${id}`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to delete comment'
            }
        }
    },

    async getCommentCount(blogId) {
        try {
            const response = await api.get(`/comments/blog/${blogId}/count`)
            return {
                success: true,
                data: response.data
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Failed to fetch comment count'
            }
        }
    }
}