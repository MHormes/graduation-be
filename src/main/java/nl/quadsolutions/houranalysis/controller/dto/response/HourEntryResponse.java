package nl.quadsolutions.houranalysis.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class HourEntryResponse {

    private String id;
    private String projectName;
    private String activityName;
    private LocalDate date;
    private int hours;

    private String remark;

    private String typeOfHours;

    private LocalDate lastEdited;

    private LocalDate accDate;

    private LocalDate approvalDate;
}
