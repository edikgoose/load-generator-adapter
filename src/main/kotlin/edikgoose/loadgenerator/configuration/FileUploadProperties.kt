package edikgoose.loadgenerator.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties("file")
class FileUploadProperties (
    var uploadDir: String = "",
)