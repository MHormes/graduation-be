package nl.quadsolutions.houranalysis.unitTest;

import nl.quadsolutions.houranalysis.service.HourEntryService;
import nl.quadsolutions.houranalysis.service.helper.HourEntryHelper;
import nl.quadsolutions.houranalysis.service.interfaces.IEmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class HourEntryServiceUnitTests {

    @Mock
    private IEmployeeService employeeService;

    @Mock
    private HourEntryHelper hourEntryHelper;

    @InjectMocks
    private HourEntryService hourEntryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveInBatchEmptyListTest() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            hourEntryService.saveInBatch(Collections.emptyList());
        });

        assertEquals("hourEntries needs content to save", thrown.getMessage());
    }
}
