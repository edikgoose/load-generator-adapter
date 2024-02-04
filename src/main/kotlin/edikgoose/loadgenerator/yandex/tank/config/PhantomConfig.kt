package edikgoose.loadgenerator.yandex.tank.config

data class PhantomConfig(
    var address: String? = null,
    var headerHttp: String? = null,
    var headers: List<String>? = null,
    var ammoType: String? = null,
    var ammofile: String? = null,
    var uris: List<String>? = null,
    var loadProfile: LoadProfileConfig? = null
)

data class LoadProfileConfig(
    var loadType: String? = null,
    var schedule: String? = null,
)