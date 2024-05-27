package edikgoose.loadgenerator.repository

import edikgoose.loadgenerator.entity.SystemConfiguration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SystemConfigurationRepository : JpaRepository<SystemConfiguration, Long> {
    @Query(
        """
        SELECT sc from SystemConfiguration sc
        WHERE LOWER(sc.name) like lower(concat('%', LOWER(:nameFilter), '%'))
    """
    )
    fun searchConfigurations(nameFilter: String): List<SystemConfiguration>

}