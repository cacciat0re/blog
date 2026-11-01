package cn.arorms.blog.app.mappers

import cn.arorms.blog.app.entities.Tag
import cn.arorms.blog.common.responses.TagVo

fun Tag.toVo(): TagVo = TagVo(id = id, name = name, slug = slug)