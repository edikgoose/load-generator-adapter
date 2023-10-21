package edikgoose.loadgenerator.yandex.tank.config

data class YandexTankConfig(
    var phantom: PhantomConfig? = null,
    var console: ConsoleConfig? = null,
    var influx: InfluxConfig? = null
)

data class ConsoleConfig(
    var enabled: Boolean? = null
)

data class InfluxConfig(
    var enabled: Boolean? = null,
    var address: String? = null,
    var database: String? = null,
    var tank_tag: String? = null
)