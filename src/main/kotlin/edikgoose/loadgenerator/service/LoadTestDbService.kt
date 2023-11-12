package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.repository.LoadTestRepository
import org.springframework.stereotype.Service

@Service
class LoadTestDbService(
    val loadTestRepository: LoadTestRepository
) {
    fun saveLoadTest(loadTest: LoadTest) {
        loadTestRepository.save(loadTest)
    }
}