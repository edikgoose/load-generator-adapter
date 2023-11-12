package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.repository.LoadTestRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LoadTestDbService(
    val loadTestRepository: LoadTestRepository
) {
    val logger: Logger = LoggerFactory.getLogger(LoadTestDbService::class.java)

    fun saveLoadTest(loadTest: LoadTest) {
        loadTestRepository.save(loadTest)
    }

    fun updateExternalId(loadTest: LoadTest, externalId: String) {
        loadTest.externalId = externalId
        loadTestRepository.save(loadTest)
    }
}