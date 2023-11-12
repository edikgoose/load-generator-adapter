package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.configuration.GrafanaProperties
import org.springframework.stereotype.Service

/**
 * Сервис для управления дашбордами графаны
 */
@Service
class GrafanaDashboardService(
    val grafanaProperties: GrafanaProperties
) {

}