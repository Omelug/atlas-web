package cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb;

import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRlinkRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserFindRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetaisService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.FindService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.SchemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CurrentDatabase currentDatabase;
    private final CustomUserDetaisService customUserDetailsService;
    private final SchemaService schemaService;
    private final UserRepository userRepository;
    private final UDRlinkRepository udrlinkRepository;
    private final FindService findService;
    private final UserFindRepository userFindRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                new DatabaseSchemaInterceptor(
                        currentDatabase,
                        customUserDetailsService,
                        schemaService,
                        userRepository,
                        udrlinkRepository,
                        findService,
                        userFindRepository)
                ).addPathPatterns("/**").excludePathPatterns("/login");
    }
}