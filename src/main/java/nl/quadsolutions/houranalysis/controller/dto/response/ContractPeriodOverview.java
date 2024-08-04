package nl.quadsolutions.houranalysis.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContractPeriodOverview {

    int periodTotalClientHours;
    LocalDate lastFoundDate;
    List<EmployeeContractPeriodOverview> employeeOverviews;
}
