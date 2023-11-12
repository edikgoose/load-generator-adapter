package edikgoose.loadgenerator.dto

data class GrafanaResponseDto (
    var id: Int,
    var slug: String,
    var status: String,
    var uid: String,
    var url: String,
    var version: Int
)