package nl.quadsolutions.houranalysis.config.openAPI;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Security Test Controller",
                version = "v1",
                description = "This is test controller",
                contact = @Contact(url = "https://www.linkedin.com/in/maarten-hormes/", name = "Maarten Hormes")
        ))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "jwt"
)
public class OpenApiConfig {

}
