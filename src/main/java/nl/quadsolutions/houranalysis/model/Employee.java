package nl.quadsolutions.houranalysis.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    public Employee(int employeeNumber, String name, List<HourEntry> hourEntries) {
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.hourEntries = hourEntries;
    }

    @Id
    private int employeeNumber;

    private String name;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<HourEntry> hourEntries;

    //todo find solution other than this manual bool
    private boolean dictuMember;
}
