package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.exception.SessionNotFoundException
import edikgoose.loadgenerator.repository.LoadTestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Сервис для работы с БД нагрузочных тестов
 */
@Service
class LoadTestDbService(
    val loadTestRepository: LoadTestRepository
) {
    @Transactional
    fun saveLoadTest(loadTest: LoadTest) {
        loadTestRepository.save(loadTest)
    }

    @Transactional
    fun getLoadTestById(id: Long): LoadTest = loadTestRepository
        .findById(id)
        .orElseThrow { SessionNotFoundException("Test with id=$id not found") }


    @Transactional
    fun getAllLoadTests(): List<LoadTest> = loadTestRepository.findAll()
}