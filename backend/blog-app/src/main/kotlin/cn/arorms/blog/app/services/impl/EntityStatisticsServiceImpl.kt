package cn.arorms.blog.app.services.impl

import cn.arorms.blog.app.repositories.ArticleRepository
import cn.arorms.blog.app.repositories.CategoryRepository
import cn.arorms.blog.app.repositories.PictureRepository
import cn.arorms.blog.app.repositories.TagRepository
import cn.arorms.blog.app.services.EntityStatisticsService
import cn.arorms.blog.common.enums.ArticleStatus
import cn.arorms.blog.common.responses.EntityCountVo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Entity statistics service implementation
 * @version 1.2.6 2026-10-13
 * @since 2026-10-13
 */
@Service
class EntityStatisticsServiceImpl(
    private val articleRepository: ArticleRepository,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val pictureRepository: PictureRepository,
) : EntityStatisticsService {

    @Transactional(readOnly = true)
    override fun getEntityCounts(): EntityCountVo =
        EntityCountVo(
            totalArticles = articleRepository.count(),
            publishedArticles = articleRepository.countByStatus(ArticleStatus.PUBLISHED),
            draftArticles = articleRepository.countByStatus(ArticleStatus.DRAFT),
            archivedArticles = articleRepository.countByStatus(ArticleStatus.ARCHIVED),
            categories = categoryRepository.count(),
            tags = tagRepository.count(),
            pictures = pictureRepository.count(),
        )
}