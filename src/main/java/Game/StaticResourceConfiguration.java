package Game;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class StaticResourceConfiguration extends WebMvcConfigurerAdapter
{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Including all static resources.

        registry.addResourceHandler("/images/**").addResourceLocations("file:///" + System.getProperty("user.dir") + "/src/main/images/");

    }
}