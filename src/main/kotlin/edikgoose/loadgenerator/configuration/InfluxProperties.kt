package edikgoose.loadgenerator.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("load-generator-engine.influx")
data class InfluxProperties (
    var address: String = "influx",
    var port: String = "8086",
)