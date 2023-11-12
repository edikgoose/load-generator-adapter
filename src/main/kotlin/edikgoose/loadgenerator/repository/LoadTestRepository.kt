package edikgoose.loadgenerator.repository

import edikgoose.loadgenerator.entity.LoadTest
import org.springframework.data.jpa.repository.JpaRepository

interface LoadTestRepository: JpaRepository<LoadTest, Long>