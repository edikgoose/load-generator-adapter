package edikgoose.loadgenerator.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import edikgoose.loadgenerator.exception.decoder.YandexTankApiClientErrorDecoder
import edikgoose.loadgenerator.feign.GrafanaFeignClient
import edikgoose.loadgenerator.feign.YandexTankApiFeignClient
import feign.Feign
import feign.Logger
import feign.Retryer
import feign.auth.BasicAuthRequestInterceptor
import feign.jackson.JacksonDecoder
import feign.slf4j.Slf4jLogger
import org.springframework.cloud.openfeign.support.SpringMvcContract
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig(
    val objectMapper: ObjectMapper,
    val yandexTankProperties: YandexTankProperties,
    val grafanaProperties: GrafanaProperties
) {
    @Bean
    fun registerYandexTankApiClientClient(): YandexTankApiFeignClient {
        return prepareBuilder()
            .logger(Slf4jLogger(YandexTankApiFeignClient::class.java))
            .errorDecoder(getYandexTankDecoder())
            .target(YandexTankApiFeignClient::class.java, yandexTankProperties.baseUrl)
    }

    @Bean
    fun registerGrafanaClient(): GrafanaFeignClient {
        return prepareBuilder()
            .logger(Slf4jLogger(GrafanaFeignClient::class.java))
            .requestInterceptors(listOf(BasicAuthRequestInterceptor(grafanaProperties.username, grafanaProperties.password)))
            .target(GrafanaFeignClient::class.java, grafanaProperties.baseUrl)
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