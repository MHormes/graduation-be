package nl.quadsolutions.houranalysis.integrationTest;

import nl.quadsolutions.houranalysis.model.AppUser;
import nl.quadsolutions.houranalysis.model.Role;
import nl.quadsolutions.houranalysis.repository.IRoleRepository;
import nl.quadsolutions.houranalysis.repository.IUserRepository;
import nl.quadsolutions.houranalysis.service.CustomUserDetailService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//testInstance annotation allows us to use BeforeAll without it being static
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class CustomUserDetailServiceIntegrationTest {

    private final CustomUserDetailService customUserDetailService;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Autowired
    public CustomUserDetailServiceIntegrationTest(CustomUserDetailService customUserDetailService, IUserRepository userRepository, IRoleRepository roleRepository) {

        this.customUserDetailService = customUserDetailService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @BeforeAll
    void setUp() {
        Role role = new Role("ADMIN");
        Role role2 = new Role("USER");

        roleRepository.save(role);
        roleRepository.save(role2);

        AppUser appUser = new AppUser(UUID.randomUUID(), "Maarten", "admin", new ArrayList<>(List.of(role, role2)));
        userRepository.save(appUser);
    }

    @Test
    void loadByUsernameTest() {
        UserDetails details = customUserDetailService.loadUserByUsername("Maarten");

        //Check if the username is correct
        assertEquals("Maarten", details.getUsername());
        //Check if the roles are present and correct
        assertEquals(2, details.getAuthorities().size());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN")));
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER")));
    }
}
