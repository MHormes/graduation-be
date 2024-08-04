package nl.quadsolutions.houranalysis.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.quadsolutions.houranalysis.model.AppUser;
import nl.quadsolutions.houranalysis.repository.IUserRepository;
import nl.quadsolutions.houranalysis.service.interfaces.IAppUserService;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class AppUserService implements IAppUserService {

    private final IUserRepository userRepository;

    public AppUser saveUser(AppUser appUser) {
        log.info("Trying to save user: {}", appUser);

        validateUser(appUser);
        if (userRepository.findByUsername(appUser.getUsername()).isPresent()) throw new IllegalArgumentException("Username already exists");

        log.debug("Checks complete, saving user: {}", appUser);
        return userRepository.save(appUser);
    }

    public AppUser findByUsername(String username) {
        log.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username).orElse(null);
    }

    private void validateUser(AppUser appUser) {
        log.debug("Validating user: {}", appUser);
        if (appUser == null) throw new IllegalArgumentException("User cannot be null");
        if (appUser.getUsername() == null || appUser.getUsername().isEmpty()) throw new IllegalArgumentException("Username is not valid");
        if (appUser.getPassword() == null || appUser.getPassword().isEmpty()) throw new IllegalArgumentException("Password is not valid");
    }
}
