package nl.quadsolutions.houranalysis.controller.dto.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CSVObject {

    @CsvBindByName(column = "Medewerker")
    private String employeeName;
    @CsvBindByName(column = "pers_num")
    private int employeeNumber;
    @CsvBindByName(column = "project_naam")
    private String projectName;
    @CsvBindByName(column = "activiteit_naam")
    private String activityName;
    @CsvCustomBindByName(column = "datum", converter = LocalDateConverter.class)
    private LocalDate date;
    @CsvBindByName(column = "uur")
    private double hours;
    @CsvBindByName(column = "opmerking")
    private String remark;
    @CsvBindByName(column = "soortuur")
    private String typeOfHours;
    @CsvCustomBindByName(column = "bijgewerkt_tot", converter = LocalDateConverter.class)
    private LocalDate lastEdited;
    @CsvCustomBindByName(column = "acc_datum", converter = LocalDateConverter.class)
    private LocalDate accDate;
    @CsvCustomBindByName(column = "Goedgekeurd op", converter = LocalDateConverter.class)
    private LocalDate approvalDate;
}
