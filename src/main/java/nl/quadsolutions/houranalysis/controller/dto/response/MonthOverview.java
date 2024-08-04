package nl.quadsolutions.houranalysis.controller.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthOverview {

    int monthIndex;
    int year;
    double workedHours;
    double sickHours;
    double personalHours;
    double publicHolidayHours;
    double clientHoursInMonth;
    double maxWorkableHoursInMonth;

}
