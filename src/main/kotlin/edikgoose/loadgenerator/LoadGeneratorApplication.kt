package edikgoose.loadgenerator

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@OpenAPIDefinition
@EnableJpaAuditing
@EnableScheduling
class LoadGeneratorApplication

fun main(args: Array<String>) {
    runApplication<LoadGeneratorApplication>(*args)
}
