package nl.quadsolutions.houranalysis.integrationTest;

import jakarta.transaction.Transactional;
import nl.quadsolutions.houranalysis.model.HourEntry;
import nl.quadsolutions.houranalysis.service.interfaces.IHourEntryService;
import nl.quadsolutions.houranalysis.service.interfaces.IImportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//testInstance annotation allows us to use BeforeAll without it being static
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class ImportServiceIntegrationTests {

    private final IImportService importService;
    private final IHourEntryService hourEntryService;
    private final JdbcTemplate jdbcTemplate;

    private MultipartFile file;

    @Autowired
    ImportServiceIntegrationTests(IImportService importService, IHourEntryService hourEntryService, JdbcTemplate jdbcTemplate) {
        this.importService = importService;
        this.hourEntryService = hourEntryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    //helper function for beforeEach method to create CSV files
    public String convertToCSV(String[] data) {
        return String.join(",", data);
    }

    //reset the database before each test
    @BeforeAll
    void resetDatabase() {
        jdbcTemplate.execute("DELETE FROM hour_entry");
    }


    @Test
    void ImportDataTest() {
        //check if db is actually empty
        HourEntry nullCheck = hourEntryService.getFirstEntry();
        Assertions.assertNull(nullCheck);

        //ARRANGE THE CSV FILE
        List<String[]> data = new ArrayList<>();
        //insert headers
        data.add(new String[]{"Medewerker", "pers_num", "project_naam", "activiteit_naam", "datum", "uur", "opmerkingen", "soortuur", "bijgewerkt_tot", "acc_datum", "projectmanager", "Goedgekeurd door", "Goedgekeurd op"
        });

        //insert test data
        data.add(new String[]{"Alessandro Pedano", "2109018", "BLD - Alessandro Pedano", "Forced day off", "23-Aug-23", "8.00", "", "time", "23-Aug-23", "23-Aug-23", "", "Finance Quad", "4-Sep-23"});
        data.add(new String[]{"Andries Evers", "2207035", "DICTU", "Projecturen", "23-Jan-23", "8.00", "", "time", "23-Jan-23", "23-Jan-23", "", "Finance Quad", "4-Sep-23"});
        data.add(new String[]{"Bart van Mierlo", "2110024", "BLD - Bart van Mierlo", "Projecturen", "23-Jun-22", "8.00", "", "time", "23-Jun-22", "23-Jun-22", "", "Finance Quad", "29-Aug-23"
        });

        String CSV_FILE_NAME = "test.csv";
        File csvOutputFile = new File(CSV_FILE_NAME);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            data.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] content = null;
        try {
            content = Files.readAllBytes(csvOutputFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        file = new MockMultipartFile("test.csv", "test.csv", "text/csv", content);


        //call the import function to get data into the dDB
        assertDoesNotThrow(() -> importService.importData(file));

        //get first entry from database that was just inserted
        HourEntry firstEntry = hourEntryService.getFirstEntry();
        assertNotNull(firstEntry);
    }

    @Test
    void importTestDataEmptyFile() {
        //create empty file
        byte[] content = new byte[0];
        file = new MockMultipartFile("test.csv", "test.csv", "text/csv", content);

        //check if db is actually empty
        HourEntry nullCheck = hourEntryService.getFirstEntry();
        assertNull(nullCheck);

        IOException thrown = assertThrows(IOException.class, () -> importService.importData(file));

        //check if exception message is correct
        assertEquals("Error processing CSV file: hourEntries needs content to save", thrown.getMessage());
    }

    @Test
    void importTestDataFileNull() {
        //check if db is actually empty
        HourEntry nullCheck = hourEntryService.getFirstEntry();
        assertNull(nullCheck);

        //call the import function to get data into the dDB
        IOException thrown = assertThrows(IOException.class, () -> importService.importData(null));

        //check if exception message is correct
        assertEquals("Error processing CSV file: Cannot invoke \"org.springframework.web.multipart.MultipartFile.getInputStream()\" because \"file\" is null", thrown.getMessage());
    }
}
