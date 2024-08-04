package nl.quadsolutions.houranalysis.service.interfaces;

import nl.quadsolutions.houranalysis.controller.dto.request.PeriodsRequest;
import nl.quadsolutions.houranalysis.controller.dto.response.ContractPeriodOverview;
import nl.quadsolutions.houranalysis.controller.dto.response.ProjectionOverview;
import nl.quadsolutions.houranalysis.model.HourEntry;

import java.time.LocalDate;
import java.util.List;

public interface IHourEntryService {
    HourEntry getFirstEntry();

    void saveInBatch(Iterable<HourEntry> hourEntries);

    ContractPeriodOverview getContractPeriodOverview(LocalDate startDate, LocalDate endDate, String projectKey);

    List<ProjectionOverview> getProjectionOverview(List<PeriodsRequest> projectionPeriods);
}
