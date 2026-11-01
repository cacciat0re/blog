package cn.arorms.blog.common.responses

data class CloudflareViewer(val zones: List<CloudflareZone>)
data class CloudflareZone(val httpRequestsAdaptiveGroups: List<AdaptiveGroup>)

data class AdaptiveGroup(
    val dimensions: Dimensions,
    val count: Int,
    val sum: SumData
)

data class Dimensions(
    val clientCountryName: String,
    val date: String? = null
)

data class SumData(val visits: Int)