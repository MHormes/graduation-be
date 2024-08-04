package nl.quadsolutions.houranalysis.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PeriodsRequest {

    private LocalDate startDate;
    private LocalDate endDate;
}
