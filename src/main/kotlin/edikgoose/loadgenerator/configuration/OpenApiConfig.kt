package edikgoose.loadgenerator.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenApiConfig {
    @Bean
    fun myOpenAPI(): OpenAPI {
        val contact = Contact().also {
            it.email = ("edikgoose@gmail.com")
            it.name = ("Eduard Zaripov")
        }

        val info: Info = Info()
            .title("Load generator adapter")
            .contact(contact)
            .version("1.0")
            .description("This API manages load tests")

        return OpenAPI().info(info)
    }
}