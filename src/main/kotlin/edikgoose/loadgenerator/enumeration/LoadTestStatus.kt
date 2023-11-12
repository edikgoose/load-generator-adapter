package edikgoose.loadgenerator.enumeration

enum class LoadTestStatus {
    CREATED,
    RUNNING,
    STOPPED,
    FAILED,
    FINISHED
}

fun LoadTestStatus.of(yandexTankStatus: String): LoadTestStatus =
    when (yandexTankStatus) {
        "running" -> LoadTestStatus.RUNNING
        "failed" -> LoadTestStatus.FAILED
        else -> LoadTestStatus.FINISHED
    }