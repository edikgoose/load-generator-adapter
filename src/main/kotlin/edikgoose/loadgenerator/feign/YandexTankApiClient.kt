package edikgoose.loadgenerator.feign

import edikgoose.loadgenerator.dto.LoadTestStartInformation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping

interface YandexTankApiClient {
    @PostMapping(path = ["/run"], consumes = [MediaType.TEXT_PLAIN_VALUE], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun runLoadTest(config: String): LoadTestStartInformation
}

/*
2023-10-29 02:05:52 2023-10-28 23:05:52,54 "console:\n  enabled: true\ninflux:\n  address: influx\n  database: metrics\n  enabled: true\n  prefix_measurement: cbe1828b-cde8-4767-8e80-152fbf6942f8\n  tank_tag: mytank\nphantom:\n  address: http://localhost\n  header_http: '1.1'\n  headers:\n  - '[Host: http]'\n  load_profile:\n    load_type: rps\n    schedule: line(1, 100, 1m)\n  uris:\n  - /\ntelegraf:\n  enabled: false\n"]
 */