package edikgoose.loadgenerator.converter

import edikgoose.loadgenerator.dto.LoadTestParams
import edikgoose.loadgenerator.yandex.tank.config.YandexTankConfig
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml


@Component
class YandexTankConfigConverter {
    fun convert(loadTestParams: LoadTestParams): String {
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.isPrettyFlow = true

        val inputStream = this.javaClass
            .classLoader
            .getResourceAsStream("yandex.tank/config-template.yaml")

        val yaml = Yaml(options)
        val yandexTankConfig: YandexTankConfig = yaml.load(inputStream)

        yandexTankConfig.phantom?.address = loadTestParams.endpoint
        yandexTankConfig.phantom?.load_profile?.schedule = loadTestParams.loadGenerationSchedule
        yandexTankConfig.phantom?.headers = yandexTankConfig.phantom?.headers
            ?.filter { it.contains("Host") }
            ?.map {
                "[Host: ${loadTestParams.endpoint.substringBefore(':')}]"
            }

        val configString = yaml.dump(yandexTankConfig)
        return configString.substringAfter("\n")
    }
}