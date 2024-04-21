package edikgoose.loadgenerator.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("consul")
class ConsulProperties {
    var url: String = ""
    var consulEnabled: Boolean = false
}