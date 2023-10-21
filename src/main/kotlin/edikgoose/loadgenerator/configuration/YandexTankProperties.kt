package edikgoose.loadgenerator.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("load-generator-engine.yandex-tank")
class YandexTankProperties(
    var baseUrl: String = ""
)