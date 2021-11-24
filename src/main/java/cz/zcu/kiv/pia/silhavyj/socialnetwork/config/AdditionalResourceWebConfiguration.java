package cz.zcu.kiv.pia.silhavyj.socialnetwork.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/***
 * Configuration of an additional resource - images.
 * This resource holds users' profile images.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Configuration
public class AdditionalResourceWebConfiguration implements WebMvcConfigurer {

    /***
     * Adds /images as another data resource of the application
     * @param registry instance of ResourceHandlerRegistry
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("file:images/");
    }
}
