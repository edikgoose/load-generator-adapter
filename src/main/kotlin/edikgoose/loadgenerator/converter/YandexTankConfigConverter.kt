package edikgoose.loadgenerator.converter

import edikgoose.loadgenerator.dto.LoadTestParamsDto
import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import edikgoose.loadgenerator.yandex.tank.config.YandexTankConfig
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

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
        externalId = null,
        grafanaUrl = null,
        loadGeneratorEngine = this.loadGeneratorEngine,
        status = LoadTestStatus.CREATED,
        address = "${this.hostName}:${this.port}",
        uris = this.uris,
        loadScheme = this.loadScheme
    )
}


private fun validateUris(uris: List<String>): List<String> =
    uris.map { uri -> if (uri[0] != '/') "/$uri" else uri }.toList()