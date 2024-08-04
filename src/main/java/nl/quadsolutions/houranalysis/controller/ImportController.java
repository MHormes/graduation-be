package nl.quadsolutions.houranalysis.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.quadsolutions.houranalysis.model.HourEntry;
import nl.quadsolutions.houranalysis.service.interfaces.IHourEntryService;
import nl.quadsolutions.houranalysis.service.interfaces.IImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/import")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class ImportController {

    private final IImportService importService;
    private final IHourEntryService hourEntryService;

    /**
     * Import HourEntries from a CSV file
     * @param file The CSV file to import
     * @return A status code and message depending on the outcome of the import
     */
    @PostMapping
    public ResponseEntity<String> importData(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            String fileName = file.getOriginalFilename();
            assert fileName != null : "File name is null";
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);

            if (fileType.equalsIgnoreCase("csv")) {
                importService.importData(file);
                return ResponseEntity.status(HttpStatus.OK).body("CSV file imported");
            } else  {
                //if other file types are added, this is the place to add them
                throw new IllegalArgumentException("File type not supported");
            }
        } catch (Exception e) {
            log.error("Something went wrong during the import of: {}", e.getMessage());
            throw e;
        }
    }
}
