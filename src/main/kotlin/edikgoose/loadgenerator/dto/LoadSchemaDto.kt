package edikgoose.loadgenerator.dto

import kotlin.time.Duration

sealed class LoadSchemaDto(val duration: Duration)

class LineLoadSchemaDto(val startRps: Long, val finalRps: Long, duration: Duration): LoadSchemaDto(duration = duration)
class ConstLoadSchemaDto(val constRps: Long, duration: Duration): LoadSchemaDto(duration = duration)
class StepLoadSchemaDto(val startRps: Long, val finalRps: Long, val step: Long, duration: Duration): LoadSchemaDto(duration = duration)
