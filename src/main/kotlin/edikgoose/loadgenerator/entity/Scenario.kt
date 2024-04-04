package edikgoose.loadgenerator.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", nullable = true)
    var ammo: Ammo?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", nullable = true)
    var systemConfiguration: SystemConfiguration?,

    @Column(name = "yandex_tank_config", nullable = false)
    var yandexTankConfig: String?,

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    var createdDate: Instant?
)

