package nl.quadsolutions.houranalysis.controller.dto.mappers;

import lombok.extern.slf4j.Slf4j;
import nl.quadsolutions.houranalysis.controller.dto.response.HourEntryResponse;
import nl.quadsolutions.houranalysis.model.HourEntry;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HourEntryMapper {

    private HourEntryMapper() {
    }

    public static HourEntryResponse mapToHourEntryResponse(HourEntry hourEntry) {
        if (hourEntry == null) {
            return null;
        }

        log.debug("Mapping hour entry to hour entry response, hour entry: {}", hourEntry.getId());

        return HourEntryResponse.builder()
                .id(hourEntry.getId().toString())
                .projectName(hourEntry.getProjectName())
                .activityName(hourEntry.getActivityName())
                .date(hourEntry.getDate())
                .hours(hourEntry.getHours())
                .remark(hourEntry.getRemark())
                .typeOfHours(hourEntry.getTypeOfHours())
                .lastEdited(hourEntry.getLastEdited())
                .accDate(hourEntry.getAccDate())
                .approvalDate(hourEntry.getApprovalDate())
                .build();
    }
}
