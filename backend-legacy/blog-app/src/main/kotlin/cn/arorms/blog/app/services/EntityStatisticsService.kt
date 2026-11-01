package cn.arorms.blog.app.services

import cn.arorms.blog.common.responses.EntityCountVo

/**
 * Entity statistics service interface
 * @version 1.2.6 2026-10-13
 * @since 2026-10-13
 */
interface EntityStatisticsService {

    /**
     * Count articles, categories and tags in the database without loading the entities
     */
    fun getEntityCounts(): EntityCountVo
}
