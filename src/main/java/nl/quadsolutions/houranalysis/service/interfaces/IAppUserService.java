package nl.quadsolutions.houranalysis.service.interfaces;

import nl.quadsolutions.houranalysis.model.AppUser;

public interface IAppUserService {

    AppUser saveUser(AppUser appUser);

    AppUser findByUsername(String username);
}
