package edikgoose.loadgenerator.dto

import edikgoose.loadgenerator.enumeration.LoadGeneratorEngine

data class LoadTestParams(
    val endpoint: String,
    val loadGeneratorEngine: LoadGeneratorEngine,
    val loadGenerationSchedule: String,
)
