package edikgoose.loadgenerator.feign

import edikgoose.loadgenerator.dto.GrafanaResponseDto
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping

interface GrafanaFeignClient {
    @PostMapping(
        "/api/dashboards/db",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun pushDashboard(dashboardConfig: String): GrafanaResponseDto
}