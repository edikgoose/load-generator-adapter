package edikgoose.loadgenerator.dto

import edikgoose.loadgenerator.enumeration.LoadGeneratorEngine
data class LoadTestParamsDto(
    val hostName: String,
    val port: String,
    val uris: Array<String>,
    val loadGeneratorEngine: LoadGeneratorEngine,
    val loadScheme: String
)