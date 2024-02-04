package edikgoose.loadgenerator.exception

class FileStorageException (message: String): RuntimeException(message) {
    constructor(message: String, cause: Throwable) : this("$message: $cause")
}
