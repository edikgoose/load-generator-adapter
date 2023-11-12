package edikgoose.loadgenerator.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("load-generator-engine.grafana")
class GrafanaProperties(
    var baseUrl: String = "",
    var username: String = "",
    var password: String = ""
)