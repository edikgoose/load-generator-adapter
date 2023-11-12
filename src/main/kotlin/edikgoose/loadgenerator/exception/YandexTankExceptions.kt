package edikgoose.loadgenerator.exception

class YandexTankException(message: String) : RuntimeException(message)
class AnotherSessionIsRunningException(message: String) : RuntimeException(message)

class SessionAlreadyStoppedException(message: String) : RuntimeException(message)

class SessionNotFoundException(message: String) : RuntimeException(message)
