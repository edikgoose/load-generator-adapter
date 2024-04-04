package edikgoose.loadgenerator.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE
import edikgoose.loadgenerator.yandex.tank.config.YandexTankConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.yaml.snakeyaml.Yaml
import jakarta.annotation.PostConstruct

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

    fun String.getConfigAsMap(): MutableMap<String, Any> =
        yaml.load<Map<String, Any>>(this).toMutableMap()
}