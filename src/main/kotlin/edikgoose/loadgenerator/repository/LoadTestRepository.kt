package edikgoose.loadgenerator.repository

import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import org.springframework.data.jpa.repository.JpaRepository

interface LoadTestRepository: JpaRepository<LoadTest, Long> {
    fun findByStatus(string: LoadTestStatus): List<LoadTest>
}