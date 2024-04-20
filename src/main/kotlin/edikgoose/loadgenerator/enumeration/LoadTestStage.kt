package edikgoose.loadgenerator.enumeration

enum class LoadTestStage {
    LOCK,
    INIT,
    CONFIGURE,
    PREPARE,
    START,
    POLL,
    END,
    POSTPROCESS,
    UNLOCK,
    FINISHED,
    UNKNOWN
}