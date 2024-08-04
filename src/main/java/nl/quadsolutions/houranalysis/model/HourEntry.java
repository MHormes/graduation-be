package nl.quadsolutions.houranalysis.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints =
        @UniqueConstraint(name = "uniqueEntry", columnNames = { "employee_id", "activityName", "date", "remark" }))
public class HourEntry{

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false, referencedColumnName = "employeeNumber")
    private Employee employee;

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
