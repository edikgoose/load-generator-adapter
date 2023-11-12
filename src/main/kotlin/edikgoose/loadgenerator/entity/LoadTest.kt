package edikgoose.loadgenerator.entity

import com.vladmihalcea.hibernate.type.array.StringArrayType
import edikgoose.loadgenerator.enumeration.LoadGeneratorEngine
import edikgoose.loadgenerator.enumeration.LoadTestStatus
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.*

@Entity
@Table(name = "load_test")
@TypeDef(name = "string-array", typeClass = StringArrayType::class)
data class LoadTest(
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "load_gen"
    )
    @SequenceGenerator(name = "load_gen", sequenceName = "load_test_id_seq", allocationSize = 1)
    var id: Long? = null,

    @Column(name = "load_generator_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var loadGeneratorEngine: LoadGeneratorEngine,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: LoadTestStatus,

    @Column(name = "external_id")
    var externalId: String?,

    @Column(name = "grafana_url")
    var grafanaUrl: String?,

    @Column(name = "address", nullable = false)
    var address: String?,

    @Type(type = "string-array")
    @Column(name = "uris", nullable = false)
    var uris: Array<String>,

    @Column(name = "load_scheme", nullable = false)
    var loadScheme: String,
)