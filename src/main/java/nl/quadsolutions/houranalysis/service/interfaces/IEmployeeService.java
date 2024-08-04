package nl.quadsolutions.houranalysis.service.interfaces;

import nl.quadsolutions.houranalysis.model.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IEmployeeService {

    Employee saveEmployee(Employee employee);
    Employee getEmployeeByEmployeeNumber(int employeeNumber);
    Employee getEmployeeByEmployeeName(String employeeName);
    List<Employee> getEmployeesWithHourEntriesForContractPeriods(LocalDate startDate, LocalDate endDate, String projectKey);
    List<Employee> getEmployeesWithHourEntriesForProjection(LocalDate projectionStartDate, LocalDate projectionEndDate);
    List<Employee> getEmployeesForDictu();
    LocalDate findLastEntryDate();
}
