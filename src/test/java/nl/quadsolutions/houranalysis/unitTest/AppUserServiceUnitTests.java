package nl.quadsolutions.houranalysis.unitTest;

import nl.quadsolutions.houranalysis.model.AppUser;
import nl.quadsolutions.houranalysis.service.AppUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AppUserServiceUnitTests {

    private final AppUserService appUserService;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    AppUserServiceUnitTests(AppUserService appUserService, BCryptPasswordEncoder encoder) {
        this.appUserService = appUserService;
        this.encoder = encoder;
    }

    @Test
    void saveUserNonValidUsernameTest() {
        //create user with empty name
        AppUser appUser = new AppUser(UUID.randomUUID(), "", encoder.encode("testing"), new ArrayList<>());


        //Check if IllegalArgumentException has been thrown
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            //save user
            appUserService.saveUser(appUser);
        });

        //check if exception message is correct
        assertEquals("Username is not valid", thrown.getMessage());
    }

    @Test
    void saveUserNonValidPasswordTest() {
        //create user with empty name
        AppUser appUser = new AppUser(UUID.randomUUID(), "testing", "", new ArrayList<>());


        //Check if IllegalArgumentException has been thrown
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            //save user
            appUserService.saveUser(appUser);
        });

        //check if exception message is correct
        assertEquals("Password is not valid", thrown.getMessage());
    }

    @Test
    void saveNullUserTest() {
        //Check if IllegalArgumentException has been thrown
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            //save user
            appUserService.saveUser(null);
        });

        //check if exception message is correct
        assertEquals("User cannot be null", thrown.getMessage());
    }
}