package edikgoose.loadgenerator.dto

import edikgoose.loadgenerator.enumeration.LoadTestStatus

data class LoadTestStartInformationDto(
    val session: String,
    var status: LoadTestStatus?,
    var grafanaUrl: String?
)