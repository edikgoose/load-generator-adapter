package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.configuration.GrafanaProperties
import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.exception.GrafanaDashboardPushException
import edikgoose.loadgenerator.feign.GrafanaFeignClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * Сервис для управления дашбордами графаны
 */
@Service
class GrafanaDashboardService(
    val grafanaFeignClient: GrafanaFeignClient,
    val grafanaProperties: GrafanaProperties
) {
    val logger: Logger = LoggerFactory.getLogger(GrafanaDashboardService::class.java)
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(GRAFANA_DATE_PATTERN)

    /**
     * Берет паттерн конфигурации дашборда графаны и подставляет туда нужные значения.
     * Далее пушит получившийся дашборд в саму графану
     */
    fun createDashboard(loadTest: LoadTest, duration: Duration): String {
        try {
            val dashboardConfig = makeDashboardConfig(loadTest.id!!, loadTest.externalId!!, duration)
            logger.info("Grafana dashboard config: $dashboardConfig")
            return grafanaProperties.baseUrl + grafanaFeignClient.pushDashboard(dashboardConfig).url
        } catch (e: RuntimeException) {
            logger.error("Error during grafana dashboard push. The test will be rollback", e)
            throw GrafanaDashboardPushException("Error during grafana dashboard push. The test will be rollback", e)
        }
    }

    /**
     * Шаблон дашборда берется из папки resources.
     * В нем подменяются параметры текущего теста
     * @param id идентификатор теста
     * @param uid идентификатор теста в сервисе генерации нагрузки
     * @param duration промежуток исполнения теста (нужно, что задать в графане правильный промежуток просмотра графиков)
     * @return готовая конфигурация дашборда
     */
    private fun makeDashboardConfig(id: Long, uid: String, duration: Duration): String {
        val inputStream = this.javaClass.classLoader.getResourceAsStream("grafana/dashboard-template.json")

        val text = inputStream?.bufferedReader().use { it?.readText() ?: "" }

        val dateNow = LocalDateTime.now(ZoneId.of("Europe/Moscow"))

        return text
            .replace("{id}", id.toString())
            .replace("{uid}", uid.take(30))
            .replace("{date-from}", dateFormatter.format(dateNow))
            .replace("{date-to}", dateFormatter.format(dateNow.plus(duration.toJavaDuration()).plusMinutes(1)))
    }

    companion object {
        const val GRAFANA_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss"
    }
}