package cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRlinkRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetaisService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.SchemaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Component
@Slf4j
public class DatabaseSchemaInterceptor implements HandlerInterceptor {

    private final CurrentDatabase currentDatabase;
    private final CustomUserDetaisService customUserDetailsService;
    private final SchemaService schemaService;
    private final UserRepository userRepository;
    private final UDRlinkRepository udrlinkRepository;

    @Autowired
    public DatabaseSchemaInterceptor(CurrentDatabase currentDatabase, CustomUserDetaisService customUserDetailsService, SchemaService schemaService, UserRepository userRepository, UDRlinkRepository udrlinkRepository) {
        this.currentDatabase = currentDatabase;
        this.customUserDetailsService = customUserDetailsService;
        this.schemaService = schemaService;
        this.userRepository = userRepository;
        this.udrlinkRepository = udrlinkRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Principal principal = request.getUserPrincipal();
        log.info("Principal is "+ principal);
        if (principal != null) {
            User user = userRepository.findByName(principal.getName()).orElse(null);
            CurrentDatabase.setCurrentDatabase(user.getCurrentDB_name());
            schemaService.updateSchema();
        }
        return true;
    }
}





