package edikgoose.loadgenerator.dto

data class FileOutputDto(
    val fileName: String,
    val fileDownloadUri: String,
    val fileType: String,
    val size: Long
)