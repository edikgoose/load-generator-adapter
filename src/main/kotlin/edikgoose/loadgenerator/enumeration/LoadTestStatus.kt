package edikgoose.loadgenerator.enumeration

enum class LoadTestStatus {
    CREATED,
    LOCKED, // значит тест специально заблокирован на определенном этапе (например, для загрузки файлов)
    RUNNING,
    STOPPED,
    FAILED,
    FINISHED
}