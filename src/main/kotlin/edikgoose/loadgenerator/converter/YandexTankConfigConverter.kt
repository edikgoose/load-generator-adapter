package edikgoose.loadgenerator.converter

import edikgoose.loadgenerator.dto.LoadTestParamsDto
import edikgoose.loadgenerator.yandex.tank.config.YandexTankConfig
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml


@Component
class YandexTankConfigConverter {
    fun convert(loadTestParamsDto: LoadTestParamsDto, id: String): String {
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.isPrettyFlow = true

        val inputStream = this.javaClass
            .classLoader
            .getResourceAsStream("yandex.tank/config-template.yaml")

        val yaml = Yaml(options)
        val yandexTankConfig: YandexTankConfig = yaml.load(inputStream)

        yandexTankConfig.influx?.prefix_measurement = id
        yandexTankConfig.phantom?.address = "${loadTestParamsDto.hostName}:${loadTestParamsDto.port}"
        yandexTankConfig.phantom?.load_profile?.schedule = loadTestParamsDto.loadScheme
        yandexTankConfig.phantom?.uris = validateUris(loadTestParamsDto.uris.toList())
        yandexTankConfig.phantom?.headers = yandexTankConfig.phantom?.headers
            ?.filter { it.contains("Host") }
            ?.map {
                "[Host: ${yandexTankConfig.phantom?.address?.substringBefore(':')}]"
            }

        val configString = yaml.dump(yandexTankConfig)

        return configString.substringAfter("\n")
    }

    private fun validateUris(uris: List<String>): List<String> =
         uris
             .map { uri -> if (uri[0] != '/') "/$uri" else uri }
             .toList()
}