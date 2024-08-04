package nl.quadsolutions.houranalysis.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.quadsolutions.houranalysis.model.Employee;
import nl.quadsolutions.houranalysis.repository.IEmployeeRepository;
import nl.quadsolutions.houranalysis.service.interfaces.IEmployeeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final IEmployeeRepository employeeRepository;

    @Override
    public Employee saveEmployee(Employee employee) {
        this.validateEmployee(employee);

        if(this.getEmployeeByEmployeeNumber(employee.getEmployeeNumber()) != null) {
            throw new IllegalArgumentException("Employee number already exists");
        }

        log.info("Saving new employee with id: {}", employee.getEmployeeNumber());

        return employeeRepository.save(employee);
    }

    private void validateEmployee(Employee employee) {
        log.debug("Validating employee: {}", employee);
        if(employee == null) throw new IllegalArgumentException("Employee is null");
        if(employee.getName() == null || employee.getName().isEmpty()) throw new IllegalArgumentException("Employee name is not valid: " + employee.getName());
        if(employee.getEmployeeNumber() == 0) throw new IllegalArgumentException("Employee number is not valid");
        if(employee.getHourEntries() == null) throw new IllegalArgumentException("Hour entries are null (can be empty)");
    }

    @Override
    public Employee getEmployeeByEmployeeNumber(int employeeNumber) {
        log.info("Getting employee by employee number: {}", employeeNumber);

        return employeeRepository.getEmployeeByEmployeeNumber(employeeNumber);
    }

    @Override
    public Employee getEmployeeByEmployeeName(String employeeName) {
        log.info("Getting employee by employee name: {}", employeeName);

        return employeeRepository.getEmployeeByName(employeeName);
    }

    @Override
    public List<Employee> getEmployeesWithHourEntriesForContractPeriods(LocalDate startDate, LocalDate endDate, String projectKey) {
        log.info("Getting hour entries between dates: {} and {} for project: {}", startDate, endDate, projectKey);
        return employeeRepository.getHourEntriesForContractPeriods(startDate, endDate, projectKey);
    }

    @Override
    public List<Employee> getEmployeesWithHourEntriesForProjection(LocalDate projectionStartDate, LocalDate projectionEndDate) {
        log.info("Getting projection between dates: {} and {}", projectionStartDate, projectionEndDate);
        return employeeRepository.getHourEntriesForProjection(projectionStartDate, projectionEndDate);
    }

    @Override
    public List<Employee> getEmployeesForDictu() {
        log.info("Getting employees for project: DICTU");
        return employeeRepository.getEmployeesForDictu();
    }

    @Override
    public LocalDate findLastEntryDate() {
        log.info("Finding last entry date");
        return employeeRepository.findLastEntryDate();
    }
}
