package nl.quadsolutions.houranalysis.unitTest;

import nl.quadsolutions.houranalysis.controller.dto.response.EmployeeContractPeriodOverview;
import nl.quadsolutions.houranalysis.controller.dto.response.MonthOverview;
import nl.quadsolutions.houranalysis.model.Employee;
import nl.quadsolutions.houranalysis.model.HourEntry;
import nl.quadsolutions.houranalysis.service.helper.HourEntryHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HourEntryHelperUnitTests {
    private final HourEntryHelper hourEntryHelper;

    @Autowired
    public HourEntryHelperUnitTests(HourEntryHelper hourEntryHelper) {
        this.hourEntryHelper = hourEntryHelper;
    }
    @Test
    void validateDatesValidDateTest() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        assertDoesNotThrow(() -> hourEntryHelper.validateDates(startDate, endDate));
    }

    @Test
    void validateDatesNullDateTest() {
        LocalDate startDate = null;
        LocalDate endDate = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hourEntryHelper.validateDates(startDate, endDate);
        });
        assertEquals("Start date and end date cannot be null", exception.getMessage());
    }

    @Test
    void validateDatesStartDateAfterEndDateTest() {
        LocalDate startDate = LocalDate.of(2023, 12, 31);
        LocalDate endDate = LocalDate.of(2023, 1, 1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hourEntryHelper.validateDates(startDate, endDate);
        });
        assertEquals("Start date cannot be after end date", exception.getMessage());
    }
}
