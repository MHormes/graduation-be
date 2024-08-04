package nl.quadsolutions.houranalysis.integrationTest;

import jakarta.transaction.Transactional;
import nl.quadsolutions.houranalysis.model.AppUser;
import nl.quadsolutions.houranalysis.service.interfaces.IAppUserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //set the test order
@Transactional
class AppUserServiceIntegrationTests {

    private final IAppUserService appUserService;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    AppUserServiceIntegrationTests(IAppUserService appUserService, BCryptPasswordEncoder encoder) {
        this.appUserService = appUserService;
        this.encoder = encoder;
    }

    @Test
    @Order(1)
    void saveUserSuccessTest(){
        AppUser user = new AppUser(UUID.randomUUID(), "testUser", encoder.encode("testing"), new ArrayList<>());

        AppUser saveReturn = appUserService.saveUser(user);

        assertNotNull(saveReturn);
        assertEquals(user.getId(), saveReturn.getId());
    }

    @Test
    @Order(2)
    void saveUserSameUsernameTest(){
        AppUser user1 = new AppUser(UUID.randomUUID(), "testUser", encoder.encode("testing"), new ArrayList<>());
        AppUser user2 = new AppUser(UUID.randomUUID(), "testUser", encoder.encode("testing"), new ArrayList<>());

        appUserService.saveUser(user1);

        //Check if IllegalArgumentException has been thrown
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            //save user
            appUserService.saveUser(user2);

        });

        //check if exception message is correct
        assertEquals("Username already exists", thrown.getMessage());
    }


    @Test
    void getUserByUsernameTest(){
        AppUser user = new AppUser(UUID.randomUUID(), "testUser", encoder.encode("testing"), new ArrayList<>());
        appUserService.saveUser(user);

        AppUser fetchedUser = appUserService.findByUsername("testUser");

        assertNotNull(fetchedUser);
        assertEquals(user.getId(), fetchedUser.getId());
    }

    @Test
    void getUserByEmptyUsernameNullTest(){
        AppUser user = new AppUser(UUID.randomUUID(), "testUser", encoder.encode("testing"), new ArrayList<>());
        appUserService.saveUser(user);

        AppUser fetchedUser = appUserService.findByUsername("");

        assertNull(fetchedUser);
    }


}
