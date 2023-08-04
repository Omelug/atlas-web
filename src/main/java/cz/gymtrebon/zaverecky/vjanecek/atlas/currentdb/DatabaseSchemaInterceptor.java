package cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.UserFind;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserFindRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseSchemaInterceptor implements HandlerInterceptor {

    private final SchemaService schemaService;
    private final UserRepository userRepository;
    private final UserFindRepository userFindRepository;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        Principal principal = request.getUserPrincipal();
        log.info("Principal is "+ principal);
        if (principal != null) {
            User user = userRepository.findByName(principal.getName()).orElse(null);
            CurrentDatabase.setCurrentDatabase(user.getCurrentDB_name());
            schemaService.updateSchema();

            String openValue = request.getParameter("open");
            if (openValue != null) {
                Optional<UserFind> userFind = userFindRepository.findByUserName(principal.getName());
                UserFind find = userFind.orElse(null);
                if (find != null) {
                    userFind.get().setOpen(Boolean.parseBoolean(openValue));
                    userFindRepository.save(userFind.get());
                }
            }
        }
        return true;
    }
}





