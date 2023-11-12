package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.configuration.GrafanaProperties
import edikgoose.loadgenerator.feign.GrafanaFeignClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

/**
 * Сервис для управления дашбордами графаны
 */
@Service
class GrafanaDashboardService(
    val grafanaFeignClient: GrafanaFeignClient,
    val grafanaProperties: GrafanaProperties
) {
    val logger: Logger = LoggerFactory.getLogger(GrafanaDashboardService::class.java)

    fun createDashboard(id: Long): String {
        val file: File = ResourceUtils.getFile("classpath:grafana/dashboard-template.json")
        val dashboardConfig = file.readText(UTF_8).replace("{id}", id.toString())
        logger.info("Grafana dashboard config: $dashboardConfig")
        return grafanaProperties.baseUrl + grafanaFeignClient.pushDashboard(dashboardConfig).url
    }

}