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