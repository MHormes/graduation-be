package nl.quadsolutions.houranalysis.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProjectionPeriodsRequestList {
    private List<PeriodsRequest> projectionPeriods;
}
