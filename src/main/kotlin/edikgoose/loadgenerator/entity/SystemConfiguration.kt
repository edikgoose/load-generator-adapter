package edikgoose.loadgenerator.entity

import edikgoose.loadgenerator.enumeration.SystemConfigurationType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "system_configuration")
@EntityListeners(AuditingEntityListener::class)
data class SystemConfiguration (
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "system_configuration_generator"
    )
    @SequenceGenerator(name = "system_configuration_generator", sequenceName = "system_configuration_id_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String?,

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    var type: SystemConfigurationType?,

    @Column(name = "configuration")
    var configuration: String?,

    @Column(name = "consul_key")
    var consulKey: String?,

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    var createdDate: Instant?
)
