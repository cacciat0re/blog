package cn.arorms.blog.app.mappers

import cn.arorms.blog.app.entities.Category
import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.common.responses.CategoryVo

fun Category.toVo(lang: Language = Language.EN): CategoryVo {
    val categoryName = this.names[lang]
        ?: this.names[Language.EN]
        ?: this.names.values.firstOrNull()
        ?: ""

    return CategoryVo(
        id = this.id,
        name = categoryName,
        slug = this.slug,
        parentId = this.parent?.id
    )
}