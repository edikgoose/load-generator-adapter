package edikgoose.loadgenerator.yandex.tank.config

data class PhantomConfig(
    var address: String? = null,
    var header_http: String? = null,
    var headers: List<String>? = null,
    var uris: List<String>? = null,
    var load_profile: LoadProfileConfig? = null
)

data class LoadProfileConfig(
    var load_type: String? = null,
    var schedule: String? = null,
)
