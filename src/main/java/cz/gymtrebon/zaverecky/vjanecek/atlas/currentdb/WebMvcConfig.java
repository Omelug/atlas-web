package cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb;

import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRlinkRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetaisService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final CurrentDatabase currentDatabase;
    private final CustomUserDetaisService customUserDetailsService;
    private final SchemaService schemaService;
    private final UserRepository userRepository;
    private final UDRlinkRepository udrlinkRepository;

    @Autowired
    public WebMvcConfig(CurrentDatabase currentDatabase, CustomUserDetaisService customUserDetailsService, SchemaService schemaService, UserRepository userRepository, UDRlinkRepository udrlinkRepository) {
        this.currentDatabase = currentDatabase;
        this.customUserDetailsService = customUserDetailsService;
        this.schemaService = schemaService;
        this.userRepository = userRepository;
        this.udrlinkRepository = udrlinkRepository;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DatabaseSchemaInterceptor(currentDatabase, customUserDetailsService, schemaService, userRepository, udrlinkRepository)).addPathPatterns("/**").excludePathPatterns("/login");
    }
}