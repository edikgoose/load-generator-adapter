package edikgoose.loadgenerator.entity

import com.vladmihalcea.hibernate.type.array.StringArrayType
import edikgoose.loadgenerator.enumeration.LoadTestStage
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import org.hibernate.annotations.TypeDef
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "load_test")
@EntityListeners(AuditingEntityListener::class)
@TypeDef(name = "string-array", typeClass = StringArrayType::class)
data class LoadTest(
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "load_gen"
    )
    @SequenceGenerator(name = "load_gen", sequenceName = "load_test_id_seq", allocationSize = 1)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", nullable = false)
    var scenario: Scenario,

    @Column(name = "name", nullable = true)
    var name: String? = null,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: LoadTestStatus,

    @Column(name = "external_id")
    var externalId: String?,

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    var createdDate: Instant?,

    @Column(name = "finish_date")
    var finishDate: Instant?,

    @Column(name = "stage")
    @Enumerated(EnumType.STRING)
    var stage: LoadTestStage?
)