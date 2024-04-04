package edikgoose.loadgenerator.repository

import edikgoose.loadgenerator.entity.LoadTest
import edikgoose.loadgenerator.entity.SystemConfiguration
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LoadTestRepository: JpaRepository<LoadTest, Long> {
    fun findByStatus(string: LoadTestStatus): List<LoadTest>

    @Query("""
            SELECT ld from LoadTest ld 
            WHERE ld.status in :statuses
    """)
    fun findByStatuses(statuses: List<LoadTestStatus>): List<LoadTest>

    @Query("""
        SELECT ld.scenario.systemConfiguration from LoadTest ld 
        WHERE ld.id = :id
    """)
    fun findConfigurationById(id: Long): SystemConfiguration?
}