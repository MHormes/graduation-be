package nl.quadsolutions.houranalysis.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import nl.quadsolutions.houranalysis.controller.dto.request.PeriodsRequest;
import nl.quadsolutions.houranalysis.controller.dto.request.ProjectionPeriodsRequestList;
import nl.quadsolutions.houranalysis.controller.dto.response.ContractPeriodOverview;
import nl.quadsolutions.houranalysis.controller.dto.response.ProjectionOverview;
import nl.quadsolutions.houranalysis.service.interfaces.IHourEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/hour-entries")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class HourEntryController {

    private final IHourEntryService hourEntryService;

    /**
     * Get an overview of a contract period
     * Currently with hardcoded dates and project key
     * @return An object containing period and employee specific items
     */
    @PostMapping("/period-overview")
    public ResponseEntity<ContractPeriodOverview> getContractPeriodOverview(
            @RequestBody PeriodsRequest periodsRequest
    ){
        return ResponseEntity.ok(hourEntryService.getContractPeriodOverview(periodsRequest.getStartDate(), periodsRequest.getEndDate(), "DICTU"));
    }


    /**
     * Get a projection of a period
     * Currently with hardcoded dates
     * @return An object containing period and employee specific items
     */
    @PostMapping("/projection-overview")
    public ResponseEntity<List<ProjectionOverview>> getProjectionOverview(
            @RequestBody ProjectionPeriodsRequestList projectionPeriods
    ){
        return ResponseEntity.ok(hourEntryService.getProjectionOverview(projectionPeriods.getProjectionPeriods()));
    }
}
