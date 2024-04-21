package edikgoose.loadgenerator.job

import edikgoose.loadgenerator.configuration.ConsulProperties
import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import edikgoose.loadgenerator.exception.SessionNotFoundException
import edikgoose.loadgenerator.repository.LoadTestRepository
import edikgoose.loadgenerator.service.LoadTestService
import edikgoose.loadgenerator.service.SystemConfigurationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class LoadTestFileUploadJob(
    private val loadTestRepository: LoadTestRepository,
    private val loadTestService: LoadTestService,
    private val systemConfigurationService: SystemConfigurationService,
    private val consulProperties: ConsulProperties
) {
    val logger: Logger = LoggerFactory.getLogger(LoadTestFileUploadJob::class.java)

    @Scheduled(fixedDelay = 300)
    fun uploadFilesAndStartJob() {
        val loadTests: List<LoadTest> = loadTestRepository.findByStatus(LoadTestStatus.LOCKED)
        for (loadTest in loadTests) {
            loadTestService.uploadFileAndStartLoadTest(loadTest)
        }
    }

    @Scheduled(fixedDelay = 3000)
    fun actualizeStatus() {
        if (!consulProperties.consulEnabled) {
            return
        }

        val loadTests: List<LoadTest> =
            loadTestRepository.findByStatuses(listOf(LoadTestStatus.RUNNING, LoadTestStatus.CREATED))
        if (loadTests.isNotEmpty()) {
            logger.info("Start job of polling load test statuses for tests: ${loadTests.map { it.id }}")
            for (loadTest in loadTests) {
                try {
                    loadTestService.getLoadTestStatus(loadTest.id!!)
                    systemConfigurationService.pollConfiguration(loadTest.id!!)
                } catch (ex: SessionNotFoundException) {
                    logger.error("Session not found, test will be deleted", ex)
                    loadTestRepository.deleteById(loadTest.id!!)
                }
            }
        }
    }
}