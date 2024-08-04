package nl.quadsolutions.houranalysis.repository;

import nl.quadsolutions.houranalysis.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<AppUser, UUID>{

    Optional<AppUser> findByUsername(String username);
}
