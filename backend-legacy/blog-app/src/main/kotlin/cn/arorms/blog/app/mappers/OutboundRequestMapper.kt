package cn.arorms.blog.app.mappers

import cn.arorms.blog.app.entities.OutboundRequest
import cn.arorms.blog.common.responses.OutboundRequestVo

fun OutboundRequest.toVo(): OutboundRequestVo = OutboundRequestVo(
    id = this.id ?: 0,
    target = this.target,
    status = this.status,
    retried = this.retried,
    payloadJson = this.payloadJson,
    expiresAt = this.expiresAt,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
