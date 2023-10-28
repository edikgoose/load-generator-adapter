package edikgoose.loadgenerator

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition
class LoadGeneratorApplication

fun main(args: Array<String>) {
	runApplication<LoadGeneratorApplication>(*args)
}
