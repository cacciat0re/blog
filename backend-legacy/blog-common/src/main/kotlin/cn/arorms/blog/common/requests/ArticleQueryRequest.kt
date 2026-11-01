package cn.arorms.blog.common.requests

import cn.arorms.blog.common.enums.ArticleStatus
import cn.arorms.blog.common.enums.Language

data class ArticleQueryRequest(
    val language: Language? = Language.EN,
    val keyword: String?,
    val categoryId: Long?,
    val articleStatus: ArticleStatus? = null
)
