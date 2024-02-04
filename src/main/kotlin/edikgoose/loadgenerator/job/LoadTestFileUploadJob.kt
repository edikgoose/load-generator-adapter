package edikgoose.loadgenerator.job

import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import edikgoose.loadgenerator.repository.LoadTestRepository
import edikgoose.loadgenerator.service.LoadTestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class LoadTestFileUploadJob(
    @Autowired private val loadTestRepository: LoadTestRepository,
    @Autowired private val loadTestService: LoadTestService
) {
    @Scheduled(fixedDelay = 300)
    fun uploadFilesAndStartJob() {
        val loadTests: List<LoadTest> = loadTestRepository.findByStatus(LoadTestStatus.LOCKED)
        for (loadTest in loadTests) {
            loadTestService.uploadFileAndStartLoadTest(loadTest)
        }
    }
}