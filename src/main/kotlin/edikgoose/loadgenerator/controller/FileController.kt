package edikgoose.loadgenerator.controller

import edikgoose.loadgenerator.dto.FileOutputDto
import edikgoose.loadgenerator.service.FileStorageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException
import javax.servlet.http.HttpServletRequest


@RestController
@Tag(name = "API для управления файлами", description = "API для управления файлами")
class FileController(
    @Autowired private val fileStorageService: FileStorageService
) {
    val logger: Logger = LoggerFactory.getLogger(FileController::class.java)

    @PostMapping("${BASE_URL}/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Загрузить файл")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<FileOutputDto> {
        val fileName: String = fileStorageService.storeFile(file)

        val fileDownloadUri: String = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/${BASE_URL}/")
            .path(fileName)
            .toUriString()

        return ResponseEntity.ok(
            FileOutputDto(
                fileName,
                fileDownloadUri,
                file.contentType ?: "no content type",
                file.size
            )
        )
    }

    @GetMapping("${BASE_URL}/{fileName:.+}")
    fun downloadFile(@PathVariable fileName: String, request: HttpServletRequest): ResponseEntity<Resource?>? {
        // Load file as Resource
        val resource: Resource = fileStorageService.loadFileAsResource(fileName)

        // Try to determine file's content type
        var contentType: String? = null
        try {
            contentType = request.servletContext.getMimeType(resource.file.absolutePath)
        } catch (ex: IOException) {
            logger.info("Could not determine file type.")
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream"
        }
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.filename + "\"")
            .body<Resource>(resource)
    }

    companion object {
        const val BASE_URL = "file"
    }
}