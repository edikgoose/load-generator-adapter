package edikgoose.loadgenerator.converter

import edikgoose.loadgenerator.dto.*
import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import edikgoose.loadgenerator.exception.YandexTankException
import edikgoose.loadgenerator.yandex.tank.config.YandexTankConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

val logger: Logger = LoggerFactory.getLogger(LoadTest::class.java)

fun LoadTest.convertToYandexTankConfig(): String {
    val options = DumperOptions()
    options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
    options.isPrettyFlow = true

    val inputStream = this.javaClass.classLoader.getResourceAsStream("yandex.tank/config-template.yaml")

    val yaml = Yaml(options)
    val yandexTankConfig: YandexTankConfig = yaml.load(inputStream)

    yandexTankConfig.influx?.prefix_measurement = "${this.id}_"
    yandexTankConfig.phantom?.address = this.address
    yandexTankConfig.phantom?.load_profile?.schedule = this.loadScheme
    yandexTankConfig.phantom?.uris = validateUris(this.uris.toList())
    yandexTankConfig.phantom?.headers = yandexTankConfig.phantom?.headers?.filter { it.contains("Host") }?.map {
        "[Host: ${yandexTankConfig.phantom?.address?.substringBefore(':')}]"
    }

    val configString = yaml.dump(yandexTankConfig)

    return configString.substringAfter("\n")
}

fun LoadTestParamsDto.convertToLoadTest(): LoadTest {
    return LoadTest(
        id = null,
        title = title,
        externalId = null,
        grafanaUrl = null,
        loadGeneratorEngine = this.loadGeneratorEngine,
        status = LoadTestStatus.CREATED,
        address = "${this.hostName}:${this.port}",
        uris = this.uris,
        loadScheme = this.loadScheme,
        createdDate = null,
    )
}


fun LoadTest.toLoadTestStatusOutputDto(): LoadTestStatusOutputDto =
    LoadTestStatusOutputDto(
        id = this.id!!,
        title = this.title,
        session = this.externalId!!,
        status = this.status,
        grafanaUrl = this.grafanaUrl!!,
        createdDate = this.createdDate!!,
    )

fun String.toLoadSchemaDto(): LoadSchemaDto {
    val type = this.takeWhile { it != '(' }
    val params = this
        .dropWhile { it != '(' }
        .drop(1)
        .takeWhile { it != ')' }
        .split(",")
        .map { it.trim() }

    val duration: Duration = params
        .last()
        .let { convertToTimeUnit(it.dropLast(1).trim().toLong(), it.takeLast(1)) }

    return when (type) {
        "line" -> LineLoadSchemaDto(
            startRps = params[0].toLong(),
            finalRps = params[1].toLong(),
            duration = duration
        )
        "const" -> ConstLoadSchemaDto(constRps = params[0].toLong(), duration = duration)
        "step" -> StepLoadSchemaDto(
            startRps = params[0].toLong(),
            finalRps = params[1].toLong(),
            step = params[2].toLong(),
            duration = duration
        )
        else -> {
            logger.error("Unexpected load generation schema type: $type")
            throw YandexTankException("Unexpected load generation schema type: $type")
        }
    }
}

private fun convertToTimeUnit(value: Long, unit: String): Duration =
    when (unit) {
        "m" -> value.minutes
        "s" -> value.seconds
        "h" -> value.hours
        else -> {
            logger.error("Unexpected time unit: $unit")
            value.hours
        }
    }


private fun validateUris(uris: List<String>): List<String> =
    uris.map { uri -> if (uri[0] != '/') "/$uri" else uri }.toList()