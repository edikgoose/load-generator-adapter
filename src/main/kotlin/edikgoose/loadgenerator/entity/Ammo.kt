package edikgoose.loadgenerator.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "ammo")
@EntityListeners(AuditingEntityListener::class)
data class Ammo (
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "ammo_generator"
    )
    @SequenceGenerator(name = "ammo_generator", sequenceName = "ammo_id_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String?,

    @Column(name = "ammo", nullable = false)
    var ammo: String?,

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    var createdDate: Instant?
)