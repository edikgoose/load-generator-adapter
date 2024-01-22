package edikgoose.loadgenerator.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "scenario")
@EntityListeners(AuditingEntityListener::class)
data class Scenario (
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "scenario_generator"
    )
    @SequenceGenerator(name = "scenario_generator", sequenceName = "scenario_id_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String?,

    @Column(name = "config", nullable = false)
    var config: String?,

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    var createdDate: Instant?
)