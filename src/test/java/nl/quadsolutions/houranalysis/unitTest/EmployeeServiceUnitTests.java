package nl.quadsolutions.houranalysis.unitTest;

import nl.quadsolutions.houranalysis.model.Employee;
import nl.quadsolutions.houranalysis.repository.IEmployeeRepository;
import nl.quadsolutions.houranalysis.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class EmployeeServiceUnitTests {

    @Mock
    private IEmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveEmployeeNull() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            Employee result = employeeService.saveEmployee(null);
        });

        assertEquals("Employee is null", thrown.getMessage());
    }

    @Test
    void saveEmployeeNonValidName() {
        Employee emp = new Employee(1234567, "", null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.saveEmployee(emp);
        });

        assertEquals("Employee name is not valid: ", thrown.getMessage());
    }

    @Test
    void saveEmployeeNonValidNumber() {
        Employee emp = new Employee(0, "Maarten", new ArrayList<>());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.saveEmployee(emp);
        });

        assertEquals("Employee number is not valid", thrown.getMessage());
    }

    @Test
    void saveEmployeeHourEntriesNull() {
        Employee emp = new Employee(1234567, "Maarten", null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.saveEmployee(emp);
        });

        assertEquals("Hour entries are null (can be empty)", thrown.getMessage());
    }
}
