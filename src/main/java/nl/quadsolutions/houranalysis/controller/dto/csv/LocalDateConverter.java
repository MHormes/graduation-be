package nl.quadsolutions.houranalysis.controller.dto.csv;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class LocalDateConverter extends AbstractBeanField<LocalDate, String> {

    //depending on mapping to CSV the format of dates can differ
    private final DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("dd-MMM-yy").withLocale(Locale.ENGLISH), // "17-oct-23"
            DateTimeFormatter.ofPattern("d-MMM-yy").withLocale(Locale.ENGLISH), // "1-oct-23"
            DateTimeFormatter.ofPattern("dd/MM/yyyy"), // "17-10-2023"
    };


    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (value.isEmpty() || value.isBlank()) {
            return null;
        }

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException e) {
                // Try the next format
            }
        }
        throw new AssertionError("Unable to parse date: " + value);
    }
}
