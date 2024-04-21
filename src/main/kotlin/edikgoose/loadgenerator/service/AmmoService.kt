package edikgoose.loadgenerator.service

import edikgoose.loadgenerator.converter.toAmmoOutputDto
import edikgoose.loadgenerator.dto.AmmoOutputDto
import edikgoose.loadgenerator.entity.Ammo
import edikgoose.loadgenerator.exception.AmmoNameAlreadyExistsException
import edikgoose.loadgenerator.exception.NotFoundException
import edikgoose.loadgenerator.repository.AmmoRepository
import org.hibernate.exception.ConstraintViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class AmmoService(
    @Autowired private val ammoRepository: AmmoRepository
) {
    val logger: Logger = LoggerFactory.getLogger(AmmoService::class.java)

    fun createAmmo(name: String, ammo: String): AmmoOutputDto {
        try {
            return ammoRepository.save(
                Ammo(id = null, name = name.trim(), ammo = ammo, createdDate = null)
            ).toAmmoOutputDto()
        } catch (e: DataIntegrityViolationException) {
            if (e.cause is ConstraintViolationException &&
                (e.cause as ConstraintViolationException).constraintName == AMMO_NAME_CONSTRAINT_NAME
            ) {
                throw AmmoNameAlreadyExistsException("Ammo file with name $name already exists")
            }
            logger.error("DataIntegrityViolationException: ammo name $name")
            throw e
        }
    }

    fun deleteAmmo(id: Long): AmmoOutputDto {
        val ammo: Ammo? = ammoRepository.findById(id).orElse(null)
        return ammo?.let {
            ammoRepository.deleteById(id)
            it.toAmmoOutputDto()
        } ?: throw NotFoundException(id, Ammo::class.java)
    }

    fun getAmmo(id: Long): AmmoOutputDto {
        val scenario: Ammo? = ammoRepository.findById(id).orElse(null)
        return scenario?.toAmmoOutputDto() ?: throw NotFoundException(id, Ammo::class.java)
    }

    fun getAllAmmo(): List<AmmoOutputDto> = ammoRepository.findAll().map { it.toAmmoOutputDto() }

    fun searchAmmo(nameFilter: String = ""): List<AmmoOutputDto> {
        return ammoRepository
            .searchAmmo(nameFilter)
            .map { it.toAmmoOutputDto() }
    }

    companion object {
        const val AMMO_NAME_CONSTRAINT_NAME = "ammo_name_idx"
    }
}