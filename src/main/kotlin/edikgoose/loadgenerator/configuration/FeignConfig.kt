package edikgoose.loadgenerator.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import edikgoose.loadgenerator.exception.decoder.YandexTankApiClientErrorDecoder
import edikgoose.loadgenerator.feign.YandexTankApiFeignClient
import feign.Feign
import feign.Logger
import feign.Retryer
import feign.jackson.JacksonDecoder
import feign.slf4j.Slf4jLogger
import org.springframework.cloud.openfeign.support.SpringMvcContract
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig(
    val objectMapper: ObjectMapper,
    val yandexTankProperties: YandexTankProperties,
) {
    @Bean
    fun registerYandexTankApiClientClient(): YandexTankApiFeignClient {
        return prepareBuilder()
            .logger(Slf4jLogger(YandexTankApiFeignClient::class.java))
            .errorDecoder(getYandexTankDecoder())
            .target(YandexTankApiFeignClient::class.java, yandexTankProperties.baseUrl)
    }

    @Bean
    fun getYandexTankDecoder(): YandexTankApiClientErrorDecoder {
        return YandexTankApiClientErrorDecoder(objectMapper)
    }

    private fun prepareBuilder(): Feign.Builder {
        return Feign.builder()
            .retryer(Retryer.NEVER_RETRY)
            .decoder(JacksonDecoder(objectMapper))
            .contract(SpringMvcContract())
            .logLevel(Logger.Level.FULL)
    }
}