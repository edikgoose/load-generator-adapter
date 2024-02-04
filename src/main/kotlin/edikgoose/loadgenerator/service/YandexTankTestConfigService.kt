package edikgoose.loadgenerator.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import edikgoose.loadgenerator.yandex.tank.config.YandexTankConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.yaml.snakeyaml.Yaml
import javax.annotation.PostConstruct
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Cервис для проверки и замены параметров к конфиге теста яндекс танка
 */
@Service
class YandexTankTestConfigService(
    @Autowired private val objectMapper: ObjectMapper
) {
    private val yaml = Yaml()

    @PostConstruct
    private fun init() {
        objectMapper.propertyNamingStrategy = SNAKE_CASE
    }

    fun parseYamlConfig(configAsString: String): YandexTankConfig {
        val config = configAsString.getConfigAsMap()
        return objectMapper.convertValue(config, YandexTankConfig::class.java)
    }

    /**
     * Подставляет в конфиге нужный префикс для названия метрик в Influx
     */
    fun substitutePrefixMeasurement(configAsString: String, newPrefix: String): String {
        val config = configAsString.getConfigAsMap()

        // Modify the value of prefix_measurement
        val influxConfig = (config["influx"] as? Map<*, *>)?.toMutableMap()
        influxConfig?.set("prefix_measurement", "${newPrefix}_")

        config["influx"] = influxConfig ?: emptyMap<Any, Any>()

        return yaml.dump(config)
    }

    /**
     * Получается время выполнения теста из конфига
     */
    fun getDuration(configAsString: String): Duration {
        val config = yaml.load<Map<String, Map<String, Map<String, Any>>>>(configAsString).toMutableMap()

        // Modify the value of prefix_measurement
        val schedule = config["phantom"]?.get("load_profile")?.get("schedule") as String

        val durationStr = schedule.substring(schedule.indexOfLast { it == ',' } + 1, schedule.indexOfLast { it == ')' }).trim()
        var duration: Duration = ZERO

        val indexOfHours = durationStr.indexOf("h")
        if (indexOfHours != -1) {
            duration = duration.plus(durationStr.substring(0, indexOfHours).toInt().hours)
        }

        val indexOfMinutes = durationStr.indexOf("m")
        if (indexOfMinutes != -1) {
            duration = duration.plus(durationStr.substring(indexOfHours + 1, indexOfMinutes).toInt().minutes)
        }

        val indexOfSeconds = durationStr.indexOf("s")
        if (indexOfSeconds != -1) {
            duration = duration.plus(durationStr.substring(indexOfMinutes + 1, indexOfSeconds).toInt().seconds)
        }

        val indexOfMilliseconds = durationStr.indexOf("ms")
        if (indexOfMilliseconds != -1) {
            duration = duration.plus(durationStr.substring(indexOfSeconds + 1, indexOfMilliseconds).toInt().milliseconds)
        }

        if (duration == ZERO) {
            duration = duration.plus(durationStr.toInt().milliseconds)
        }

        return duration
    }

    private fun parseDurationString(durationString: String): Duration {
        val regex = "(\\d+)([hms])".toRegex()
        val matches = regex.findAll(durationString)

        var duration = ZERO

        for (match in matches) {
            val (value, unit) = match.destructured

            when (unit) {
                "h" -> duration = duration.plus(value.toLong().hours)
                "m" -> duration = duration.plus(value.toLong().minutes)
                "s" -> duration = duration.plus(value.toLong().seconds)
                "ms" -> duration = duration.plus(value.toLong().milliseconds)
            }
        }

        return duration
    }

    fun String.getConfigAsMap(): MutableMap<String, Any> =
        yaml.load<Map<String, Any>>(this).toMutableMap()
}