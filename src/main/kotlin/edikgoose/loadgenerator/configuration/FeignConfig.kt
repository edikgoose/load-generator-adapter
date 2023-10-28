package edikgoose.loadgenerator.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import edikgoose.loadgenerator.feign.YandexTankApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import feign.Feign
import feign.Logger
import feign.Retryer
import feign.jackson.JacksonDecoder
import feign.slf4j.Slf4jLogger
import org.springframework.cloud.openfeign.support.SpringMvcContract

@Configuration
class FeignConfig(
    val objectMapper: ObjectMapper,
    val yandexTankProperties: YandexTankProperties
) {
    @Bean
    fun registerProfileClient(): YandexTankApiClient {
        return prepareBuilder()
            .logger(Slf4jLogger(YandexTankApiClient::class.java))
            .target(YandexTankApiClient::class.java, yandexTankProperties.baseUrl)
    }

    private fun prepareBuilder(): Feign.Builder {
        return Feign.builder()
            .retryer(Retryer.NEVER_RETRY)
            .decoder(JacksonDecoder(objectMapper))
            .contract(SpringMvcContract())
            .logLevel(Logger.Level.FULL)
    }
}