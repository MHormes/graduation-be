package nl.quadsolutions.houranalysis.controller.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeContractPeriodOverview {

    String employeeName;
    List<MonthOverview> monthOverviews;
}
