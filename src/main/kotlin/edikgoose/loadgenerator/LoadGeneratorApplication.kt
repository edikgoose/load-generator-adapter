package edikgoose.loadgenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LoadGeneratorApplication

fun main(args: Array<String>) {
	runApplication<LoadGeneratorApplication>(*args)
}
