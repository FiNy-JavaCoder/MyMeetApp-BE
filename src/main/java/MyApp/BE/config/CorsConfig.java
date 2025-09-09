package MyApp.BE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Platí pro všechny cesty API
                .allowedOrigins("http://localhost:5173") // Povolit původ React aplikace
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Povolit potřebné HTTP metody
                .allowedHeaders("*") // Povolit všechny hlavičky
                .allowCredentials(true);
    }
}