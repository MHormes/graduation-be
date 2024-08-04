package nl.quadsolutions.houranalysis.integrationTest;

import nl.quadsolutions.houranalysis.model.Employee;
import nl.quadsolutions.houranalysis.model.HourEntry;
import nl.quadsolutions.houranalysis.service.interfaces.IEmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeServiceIntegrationTest {

    private final IEmployeeService employeeService;

    @Autowired
    public EmployeeServiceIntegrationTest(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Test
    void saveEmployeeSuccessTest() {
        Employee emp = new Employee(4321, "Maarten", new ArrayList<>());

        Employee savedEmp = employeeService.saveEmployee(emp);

        assertEquals(emp.getEmployeeNumber(), savedEmp.getEmployeeNumber());
    }

    @Test
    void saveEmployeeSameNumberTest() {
        Employee emp1 = new Employee(12345, "Maarten", new ArrayList<>());
        Employee emp2 = new Employee(12345, "Chris", new ArrayList<>());

        employeeService.saveEmployee(emp1);

        //Check if IllegalArgumentException has been thrown
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            //save user
            employeeService.saveEmployee(emp2);

        });

        //check if exception message is correct
        assertEquals("Employee number already exists", thrown.getMessage());
    }

    @Test
    void getEmployeeByNumberTest() {
        int number = 987654;
        Employee emp = new Employee(number, "Maarten", new ArrayList<>());

        employeeService.saveEmployee(emp);

        Employee getEmp = employeeService.getEmployeeByEmployeeNumber(number);

        assertEquals(emp.getEmployeeNumber(), getEmp.getEmployeeNumber());
    }

    @Test
    void getEmployeesWithHourEntriesForContractPeriodsTest() {
        Employee employee = new Employee(98753269, "John Doe", new ArrayList<>(), true);

        List<HourEntry> hourEntries = List.of(
                new HourEntry(UUID.randomUUID(), employee, "DICTU", "Projecturen", LocalDate.of(2023, 1, 16), 8, "", "time", LocalDate.of(2023, 1, 16), LocalDate.of(2023, 1, 16), LocalDate.of(2023, 1, 16)),
                new HourEntry(UUID.randomUUID(), employee, "DICTU", "Projecturen", LocalDate.of(2023, 2, 15), 6, "", "time", LocalDate.of(2023, 2, 15), LocalDate.of(2023, 2, 15), LocalDate.of(2023, 2, 15))
        );
        employee.setHourEntries(hourEntries);

        employeeService.saveEmployee(employee);

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        String projectKey = "DICTU";

        List<Employee> result = employeeService.getEmployeesWithHourEntriesForContractPeriods(startDate, endDate, projectKey);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals(1, result.get(0).getHourEntries().size());
        assertEquals(8, result.get(0).getHourEntries().get(0).getHours());
        // Further assertions can be added based on the data setup
    }
}