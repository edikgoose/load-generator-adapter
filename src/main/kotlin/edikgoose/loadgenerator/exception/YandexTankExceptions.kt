package edikgoose.loadgenerator.exception

class YandexTankException(message: String) : RuntimeException(message)

class AnotherSessionIsRunningException(message: String) : RuntimeException(message)

class SessionAlreadyStoppedException(message: String) : RuntimeException(message)

class SessionNotFoundException(message: String) : RuntimeException(message)

class NotFoundException(id: Long, classs: Class<*>?) : RuntimeException("Entity ${classs?.simpleName ?: ""} with id $id not found")

class IllegalConfigException(message: String) : RuntimeException(message)
