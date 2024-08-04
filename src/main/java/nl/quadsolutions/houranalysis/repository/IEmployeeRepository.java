package nl.quadsolutions.houranalysis.repository;

import nl.quadsolutions.houranalysis.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, UUID> {

    Employee getEmployeeByEmployeeNumber(int employeeNumber);

    Employee getEmployeeByName(String employeeName);

    @Query("""
             SELECT e
             FROM Employee e
             JOIN FETCH e.hourEntries h
             WHERE e.dictuMember = true
             AND ((h.activityName = 'Projecturen' AND h.projectName like %:projectKey%) OR h.activityName <> 'Projecturen')
             AND h.date BETWEEN :startDate AND :endDate
             ORDER BY h.date
             """)
    List<Employee> getHourEntriesForContractPeriods(LocalDate startDate, LocalDate endDate, String projectKey);

    @Query("""
             SELECT e
             FROM Employee e
             JOIN FETCH e.hourEntries h
             WHERE e.dictuMember = true
             AND h.activityName <> 'Projecturen'
             AND h.date BETWEEN :projectionStartDate AND :projectionEndDate
             ORDER BY h.date
             """)
    List<Employee>getHourEntriesForProjection(LocalDate projectionStartDate, LocalDate projectionEndDate);

    @Query("""
             SELECT e
             FROM Employee e
             WHERE e.dictuMember = true
             """)
    List<Employee>getEmployeesForDictu();

    @Query("""
             SELECT MAX(h.date)
             FROM Employee e
             JOIN e.hourEntries h
             WHERE e.dictuMember = true
             AND h.activityName = 'Projecturen'
             """)
    LocalDate findLastEntryDate();
}
