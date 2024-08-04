package nl.quadsolutions.houranalysis.repository;

import nl.quadsolutions.houranalysis.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IRoleRepository extends JpaRepository<Role, UUID> {
}
