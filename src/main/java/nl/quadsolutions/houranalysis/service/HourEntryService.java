package nl.quadsolutions.houranalysis.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.quadsolutions.houranalysis.controller.dto.request.PeriodsRequest;
import nl.quadsolutions.houranalysis.controller.dto.response.ContractPeriodOverview;
import nl.quadsolutions.houranalysis.controller.dto.response.EmployeeContractPeriodOverview;
import nl.quadsolutions.houranalysis.controller.dto.response.ProjectionOverview;
import nl.quadsolutions.houranalysis.model.Employee;
import nl.quadsolutions.houranalysis.model.HourEntry;
import nl.quadsolutions.houranalysis.repository.IHourEntryRepository;
import nl.quadsolutions.houranalysis.service.helper.HourEntryHelper;
import nl.quadsolutions.houranalysis.service.interfaces.IEmployeeService;
import nl.quadsolutions.houranalysis.service.interfaces.IHourEntryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class HourEntryService implements IHourEntryService {

    private final IHourEntryRepository hourEntryRepository;
    private final IEmployeeService employeeService;
    private final HourEntryHelper hourEntryHelper;

    @Override
    public HourEntry getFirstEntry() {
        log.info("Getting first hour entry, checking if data is present");

        return hourEntryRepository.findFirstByOrderByIdAsc();
    }

    @Override
    public void saveInBatch(Iterable<HourEntry> hourEntries) {
        log.info("Saving new hour entries in batch");

        if (hourEntries == null || !hourEntries.iterator().hasNext()) {
            throw new IllegalArgumentException("hourEntries needs content to save");
        }

        hourEntryRepository.saveAll(hourEntries);
    }

    @Override
    public ContractPeriodOverview getContractPeriodOverview(LocalDate startDate, LocalDate endDate, String projectKey) {
        hourEntryHelper.validateDates(startDate, endDate);

        log.info("Getting a contract period overview");

        ContractPeriodOverview returnObject = new ContractPeriodOverview();

        List<Employee> employees =  employeeService.getEmployeesWithHourEntriesForContractPeriods(startDate, endDate, projectKey);

        List<EmployeeContractPeriodOverview> employeeOverviews = hourEntryHelper.sortOnEmployeeName(hourEntryHelper.groupPerEmployee(employees, startDate, endDate));

        returnObject.setEmployeeOverviews(employeeOverviews);

        returnObject.setPeriodTotalClientHours(hourEntryHelper.calculateTotalClientHours(employeeOverviews.get(0)));
        returnObject.setLastFoundDate(employeeService.findLastEntryDate());
        return returnObject;
    }

    @Override
    public List<ProjectionOverview> getProjectionOverview(List<PeriodsRequest> periods){
        log.info("Getting a projection overview");

        List<ProjectionOverview> returnList = new ArrayList<>();

        for(PeriodsRequest period : periods){
            hourEntryHelper.validateDates(period.getStartDate(), period.getEndDate());

            log.info("Getting projection for period: {} - {}", period.getStartDate(), period.getEndDate());
            ProjectionOverview returnObject = new ProjectionOverview();

            List<Employee> employees =  employeeService.getEmployeesWithHourEntriesForProjection(period.getStartDate(), period.getEndDate());

            if(employees.isEmpty()){
                log.info("No entries found for period: {} - {}, defaulting to DICTU-Members", period.getStartDate(), period.getEndDate());
                employees = employeeService.getEmployeesForDictu();
            }
            List<EmployeeContractPeriodOverview> employeeProjections = hourEntryHelper.sortOnEmployeeName(hourEntryHelper.groupPerEmployee(employees, period.getStartDate(), period.getEndDate()));

            returnObject.setEmployeeProjections(employeeProjections);

            returnObject.setProjectionTotalClientHours(hourEntryHelper.calculateTotalClientHours(employeeProjections.get(0)));
            returnList.add(returnObject);
        }
        return returnList;
    }
}
