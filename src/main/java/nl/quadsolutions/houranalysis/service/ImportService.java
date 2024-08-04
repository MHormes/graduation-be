package nl.quadsolutions.houranalysis.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.quadsolutions.houranalysis.controller.dto.csv.CSVObject;
import nl.quadsolutions.houranalysis.model.Employee;
import nl.quadsolutions.houranalysis.model.HourEntry;
import nl.quadsolutions.houranalysis.service.helper.HourEntryHelper;
import nl.quadsolutions.houranalysis.service.interfaces.IEmployeeService;
import nl.quadsolutions.houranalysis.service.interfaces.IHourEntryService;
import nl.quadsolutions.houranalysis.service.interfaces.IImportService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ImportService implements IImportService {

    private final IEmployeeService employeeService;
    private final IHourEntryService hourEntryService;

    @Override
    public void importData(MultipartFile file) throws IOException {
        log.info("Importing data from CSV file");
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            //map the csv file to a csv object for further transformation
            CsvToBean<CSVObject> csvToBean = new CsvToBeanBuilder<CSVObject>(reader)
                    .withType(CSVObject.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            //process the csv data into domain objects
            List<HourEntry> hourList = processCSVData(csvToBean);

            LocalDate lastFoundDate =  employeeService.findLastEntryDate();
            if(lastFoundDate == null) {
                lastFoundDate = LocalDate.of(2020, 1, 1);
            }
            // Filter the hourList to include only entries after the lastFoundDate
            LocalDate finalLastFoundDate = lastFoundDate;
            List<HourEntry> filteredHourList = hourList.stream()
                    .filter(hourEntry -> hourEntry.getDate().isAfter(finalLastFoundDate))
                    .toList();

            //save in batch to increase performance
            saveInBatch(filteredHourList);
        } catch (Exception e) {
            throw new IOException("Error processing CSV file: " + e.getMessage(), e);
        }
    }

    private List<HourEntry> processCSVData(CsvToBean<CSVObject> csvToBean) {
        List<HourEntry> returnList = new ArrayList<>();

        //save last emp number to reduce db calls
        String previousEmployeeName = "";
        Employee emp = null;

        // Process the CSV data into domain objects
        for (CSVObject csvObject : csvToBean) {
            if(!Objects.equals(previousEmployeeName, csvObject.getEmployeeName())) {
                previousEmployeeName = csvObject.getEmployeeName();
                emp = employeeService.getEmployeeByEmployeeNumber(csvObject.getEmployeeNumber());
                if (emp == null) {
                    emp = Employee.builder()
                            .employeeNumber(csvObject.getEmployeeNumber())
                            .name(csvObject.getEmployeeName())
                            .hourEntries(new ArrayList<>())
                            .build();
                    employeeService.saveEmployee(emp);
                }
            }

            HourEntry hourEntry = HourEntry.builder()
                    .id(UUID.randomUUID())
                    .employee(emp)
                    .projectName(csvObject.getProjectName())
                    .activityName(csvObject.getActivityName())
                    .date(csvObject.getDate())
                    .hours((int)csvObject.getHours())
                    .remark(csvObject.getRemark())
                    .build();

            returnList.add(hourEntry);
        }
        return returnList;
    }

    private void saveInBatch(List<HourEntry> hourList) {
        hourEntryService.saveInBatch(hourList);
    }
}
