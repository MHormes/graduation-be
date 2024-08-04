package nl.quadsolutions.houranalysis.model.seeder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.quadsolutions.houranalysis.model.AppUser;
import nl.quadsolutions.houranalysis.service.interfaces.IAppUserService;
import nl.quadsolutions.houranalysis.service.interfaces.IEmployeeService;
import nl.quadsolutions.houranalysis.service.interfaces.IHourEntryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.UUID;

@Profile("!prod")
@Configuration
@AllArgsConstructor
@Slf4j
public class Seeder {

    @Bean
    CommandLineRunner run(IAppUserService appUserService, IEmployeeService employeeService, IHourEntryService hourEntryService, BCryptPasswordEncoder encoder) {
        return args -> {
            try {
                log.info("running seeder");

                AppUser admin = appUserService.findByUsername("admin");
                if(admin == null){
                    appUserService.saveUser(new AppUser(UUID.randomUUID(), "admin", encoder.encode("admin"), new ArrayList<>()));
                }
                log.info("Seeding went successful");
            } catch (Exception ex) {
                log.error("Seeder failed on: " + ex.getMessage());
            } finally {
                log.info("Seeder finished");
            }
        };
    }
}
