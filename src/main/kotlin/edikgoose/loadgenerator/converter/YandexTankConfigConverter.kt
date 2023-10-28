package edikgoose.loadgenerator.converter

import edikgoose.loadgenerator.dto.LoadTestParams
import edikgoose.loadgenerator.yandex.tank.config.YandexTankConfig
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml


@Component
class YandexTankConfigConverter {
    fun convert(loadTestParams: LoadTestParams, id: String): String {
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.isPrettyFlow = true

        val inputStream = this.javaClass
            .classLoader
            .getResourceAsStream("yandex.tank/config-template.yaml")

        val yaml = Yaml(options)
        val yandexTankConfig: YandexTankConfig = yaml.load(inputStream)

        yandexTankConfig.influx?.prefix_measurement = id
        yandexTankConfig.phantom?.address = loadTestParams.address
        yandexTankConfig.phantom?.load_profile?.schedule = loadTestParams.loadGenerationSchedule
        yandexTankConfig.phantom?.uris = loadTestParams.uris.toList()
        yandexTankConfig.phantom?.headers = yandexTankConfig.phantom?.headers
            ?.filter { it.contains("Host") }
            ?.map {
                "[Host: ${loadTestParams.address.substringBefore(':')}]"
            }

        val configString = yaml.dump(yandexTankConfig)

        return configString.substringAfter("\n")
    }
}