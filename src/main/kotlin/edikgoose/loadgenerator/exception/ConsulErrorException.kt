package edikgoose.loadgenerator.exception

class ConsulErrorException(ex: RuntimeException) : RuntimeException("Error during request to Consul", ex)

class ConsulKeyValueNotFoundException
    : RuntimeException("Key is not found in Consul")

class ConsulKeyValueNotFoundAndConfigNotPutException(key: String)
    : RuntimeException("Consul key $key not found and new KV isn't created because config is missed")

