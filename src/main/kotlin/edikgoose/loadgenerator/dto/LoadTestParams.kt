package edikgoose.loadgenerator.dto

import edikgoose.loadgenerator.enumeration.LoadGeneratorEngine
import io.swagger.v3.oas.annotations.Parameter

data class LoadTestParams(
    @Parameter(example = "http://localhost")
    val address: String,
    val uris: Array<String>,
    val loadGeneratorEngine: LoadGeneratorEngine,
    @Parameter(name = "line(1, 100, 1m)")
    val loadGenerationSchedule: String,
)

/*
[{'autostop':
    {
        'enabled':
            True,
        'package': 'yandextank.plugins.Autostop'
    },
    'console': {'enabled': True, 'package': 'yandextank.plugins.Console'},
    'phantom': {'enabled': True, 'package': 'yandextank.plugins.Phantom'},
    'telegraf': {'enabled': True, 'package': 'yandextank.plugins.Telegraf'}
    'influx': {'enabled': False, 'package': 'yandextank.plugins.InfluxUploader'}
    "console:\n  enabled: true\n
    influx:\n  address: influx\n  database: metrics\n  enabled: true\n  prefix_measurement: 379575a7-3437-49e8-b13f-957f05cb4988\n  tank_tag: mytank\nphantom:\n  address: http://localhost\n  header_http: '1.1'\n  headers:\n  - '[Host: http]'\n  load_profile:\n    load_type: rps\n    schedule: line(1, 100, 1m)\n  uris:\n  - /\ntelegraf:\n  enabled: false\n"]
 */