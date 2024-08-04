package nl.quadsolutions.houranalysis.service.helper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.quadsolutions.houranalysis.controller.dto.response.*;
import nl.quadsolutions.houranalysis.model.Employee;
import nl.quadsolutions.houranalysis.model.HourEntry;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class HourEntryHelper {

    public List<EmployeeContractPeriodOverview> groupPerEmployee(List<Employee> employees, LocalDate startDate, LocalDate endDate) {
        List<EmployeeContractPeriodOverview> employeeContractPeriodOverviews = new ArrayList<>();
        double clientMultiplier = 7.2;
        double quadMultiplier = 8;

        for (Employee e : employees) {
            log.info("Hour entries for employee: {}", e.getName());

            String employeeName = e.getName();

            List<MonthOverview> monthOverviews = new ArrayList<>(getMonthOverviewLoop(startDate, endDate, e, clientMultiplier, quadMultiplier));

            employeeContractPeriodOverviews.add(EmployeeContractPeriodOverview.builder()
                    .employeeName(employeeName)
                    .monthOverviews(monthOverviews)
                    .build());
        }
        return employeeContractPeriodOverviews;
    }

    public List<MonthOverview> getMonthOverviewLoop(LocalDate startDate, LocalDate endDate, Employee emp, double clientMultiplier, double quadMultiplier) {
        List<MonthOverview> returnList = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            int year = currentDate.getYear();
            int month = currentDate.getMonthValue();

            MonthOverview overview = this.getMonthOverview(emp.getHourEntries(), month, year);

            YearMonth yearMonth = YearMonth.of(year, month);
            int weekdays = 0;

            LocalDate firstDayOfMonth = yearMonth.atDay(1);
            LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

            // Determine the actual start and end days for the current month
            LocalDate actualStart = currentDate.isAfter(firstDayOfMonth) ? currentDate : firstDayOfMonth;
            LocalDate actualEnd = endDate.isBefore(lastDayOfMonth) ? endDate : lastDayOfMonth;

            for (LocalDate date = actualStart; !date.isAfter(actualEnd); date = date.plusDays(1)) {
                if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    weekdays++;
                }
            }

            overview.setClientHoursInMonth(weekdays * clientMultiplier);
            overview.setMaxWorkableHoursInMonth(weekdays * quadMultiplier);
            returnList.add(overview);

            currentDate = currentDate.plusMonths(1).withDayOfMonth(1); // Move to the first day of the next month
        }

        return returnList;
    }

    public MonthOverview getMonthOverview(List<HourEntry> entries, int month, int year){
        int totalHoursWorked = 0;
        int totalHoursSick = 0;
        int totalHoursPersonal = 0;
        int totalHoursHoliday = 0;

        for (HourEntry entry : entries) {
            if (entry.getDate().getMonthValue() == month) {
                switch (entry.getActivityName()) {
                    case "Projecturen":
                        totalHoursWorked += entry.getHours();
                        break;
                    case "Ziekte":
                        totalHoursSick += entry.getHours();
                        break;
                    case "Vakantie":
                        totalHoursPersonal += entry.getHours();
                        break;
                    case "Nationale Feestdag":
                        totalHoursHoliday += entry.getHours();
                        break;
                    default:
                        //do something
                        break;
                }
            }
        }

        return MonthOverview.builder()
                .monthIndex(month)
                .year(year)
                .workedHours(totalHoursWorked)
                .sickHours(totalHoursSick)
                .personalHours(totalHoursPersonal)
                .publicHolidayHours(totalHoursHoliday)
                .build();

    }

    public void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    public List<EmployeeContractPeriodOverview> sortOnEmployeeName(List<EmployeeContractPeriodOverview> empPeriodOverview) {
        empPeriodOverview.sort(Comparator.comparing(EmployeeContractPeriodOverview::getEmployeeName));
        return empPeriodOverview;
    }

    public int calculateTotalClientHours(EmployeeContractPeriodOverview empOverview){
        int totalClientHours = 0;
        for(MonthOverview monthOverview : empOverview.getMonthOverviews()){
            totalClientHours += (int) monthOverview.getClientHoursInMonth();
        }
        return totalClientHours;
    }
}
