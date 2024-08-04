package nl.quadsolutions.houranalysis.integrationTest;


import jakarta.transaction.Transactional;
import nl.quadsolutions.houranalysis.model.Employee;
import nl.quadsolutions.houranalysis.model.HourEntry;
import nl.quadsolutions.houranalysis.service.interfaces.IEmployeeService;
import nl.quadsolutions.houranalysis.service.interfaces.IHourEntryService;
import nl.quadsolutions.houranalysis.service.interfaces.IImportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//testInstance annotation allows us to use BeforeAll without it being static
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class HourEntryServiceIntegrationTests {

    private final IImportService importService;
    private final IHourEntryService hourEntryService;
    private final IEmployeeService employeeService;

    private static final String CSV_FILE_NAME = "test.csv";

    @Autowired
    public HourEntryServiceIntegrationTests(IImportService importService, IHourEntryService hourEntryService, IEmployeeService employeeService) {
        this.importService = importService;
        this.hourEntryService = hourEntryService;
        this.employeeService = employeeService;
    }

    public String convertToCSV(String[] data) {
        return String.join(",", data);
    }

    @BeforeAll
    void setup() {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"Medewerker", "pers_num", "project_naam", "activiteit_naam", "datum", "uur", "opmerkingen", "soortuur", "bijgewerkt_tot", "acc_datum", "projectmanager", "Goedgekeurd door", "Goedgekeurd op"});

        data.add(new String[]{"Andries Evers", "2207035", "DICTU", "Projecturen", "15-Dec-23", "8.00", "", "time", "15-Dec-23", "15-Dec-23", "", "Finance Quad", "15-Dec-23"});
        data.add(new String[]{"Alessandro Pedano", "2109018", "BLD - Alessandro Pedano", "Vakantie", "17-Dec-23", "8.00", "", "time", "17-Dec-23", "17-Dec-23", "", "Finance Quad", "17-Dec-23"});
        data.add(new String[]{"Bart van Mierlo", "2110024", "BLD - Bart van Mierlo", "Projecturen", "15-Dec-23", "8.00", "", "time", "15-Dec-23", "15-Dec-23", "", "Finance Quad", "15-Dec-23"});

        File csvOutputFile = new File(CSV_FILE_NAME);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            data.stream().map(this::convertToCSV).forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] content;
        try {
            content = Files.readAllBytes(csvOutputFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MockMultipartFile file = new MockMultipartFile("test.csv", "test.csv", "text/csv", content);

        try {
            importService.importData(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getFirstHourEntryTest() {
        HourEntry entry = hourEntryService.getFirstEntry();
        Assertions.assertNotNull(entry);
    }

    @Test
    void saveInBatchTest() {
        Employee emp = employeeService.saveEmployee(new Employee(784512, "Maarten Hormes", new ArrayList<>()));

        List<HourEntry> entries = new ArrayList<>();
        entries.add(new HourEntry(UUID.randomUUID(), emp, "BLD - Maarten Hormes", "Projecturen", LocalDate.of(2023, 12, 16), 8, "", "time", LocalDate.of(2023, 12, 16), LocalDate.of(2023, 12, 16), LocalDate.of(2023, 12, 16)));
        entries.add(new HourEntry(UUID.randomUUID(), emp, "BLD - Maarten Hormes", "Projecturen", LocalDate.of(2023, 12, 15), 8, "", "time", LocalDate.of(2023, 12, 15), LocalDate.of(2023, 12, 15), LocalDate.of(2023, 12, 15)));
        entries.add(new HourEntry(UUID.randomUUID(), emp, "BLD - Maarten Hormes", "Projecturen", LocalDate.of(2023, 12, 17), 8, "", "time", LocalDate.of(2023, 12, 17), LocalDate.of(2023, 12, 17), LocalDate.of(2023, 12, 17)));

        Assertions.assertDoesNotThrow(() -> hourEntryService.saveInBatch(entries));
    }
}

