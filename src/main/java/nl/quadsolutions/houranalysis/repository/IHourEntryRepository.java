package nl.quadsolutions.houranalysis.repository;


import nl.quadsolutions.houranalysis.model.HourEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IHourEntryRepository extends JpaRepository<HourEntry, UUID> {

    HourEntry findFirstByOrderByIdAsc();
}
