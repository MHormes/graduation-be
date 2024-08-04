package nl.quadsolutions.houranalysis.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectionOverview {

    int projectionTotalClientHours;
    List<EmployeeContractPeriodOverview> employeeProjections;
}
