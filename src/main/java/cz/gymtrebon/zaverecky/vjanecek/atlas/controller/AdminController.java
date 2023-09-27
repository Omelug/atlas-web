package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.DatabaseAccess;
import cz.gymtrebon.zaverecky.vjanecek.atlas.log.LogTyp;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.DatabaseBackupService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.SchemaService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.UDRLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@SuppressWarnings("SameReturnValue")
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('" + User.ADMIN + "')")
public class AdminController {
    private final UserRepository userRepository;
    private final UDRLinkRepository udrLinkRepository;
    private final DatabaseRepository databaseRepository;
    private final RoleRepository roleRepository;
    private final SchemaService schemaService;
    private final CustomLoggerRepository customLoggerRepository;
    private final UserFindRepository userFindRepository;
    private final SpringTemplateEngine templateEngine;
    private final UDRLinkService udrLinkService;
    private final DatabaseBackupService databaseBackupService;
    @Value("${images.path}")
    private String imagesFolder;

    @GetMapping("")
    public String adminTools(Model model) {

        model.addAttribute("userList", userRepository.findAllByOrderByName());
        model.addAttribute("databaseList", databaseRepository.findAllByOrderByName());
        model.addAttribute("roleList", roleRepository.findAll());
        model.addAttribute("UDRLinkList", udrLinkRepository.findAllByOrderById());
        model.addAttribute("logList", customLoggerRepository.findAllByOrderByTimeDesc());

        return "admin/admin";
    }
    @PostMapping("/addDatabase")
    @ResponseBody
    public String addDatabase(HttpServletRequest request, HttpServletResponse response, @RequestParam("databaseName") String databaseName) {
        if (databaseName.isBlank()) {
            return "redirect:/admin";
        }
        if (databaseRepository.findByName(databaseName).isPresent()) {
            customLoggerRepository.save(
                    new LoggerLine(LogTyp.ERROR,
                            "adminTools",
                            "Database "+databaseName+" already exists"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                response.getWriter().write("Database "+databaseName+" already exists");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
        log.info("database name "+databaseName);
        Database database = new Database();
        database.setName(databaseName);
        database.setDatabaseAccess(DatabaseAccess.PUBLIC);
        databaseRepository.save(database);

        //create schema for database
        schemaService.createUserSchema(databaseName);
        schemaService.createTablesInSchema(databaseName);
        return generateTableFragmentHtml(request, response, databaseRepository.findAllByOrderByName());
    }
    @PostMapping("/deleteDatabase")
    @ResponseBody
    public String deleteDatabase(HttpServletRequest request, HttpServletResponse response, Principal principal, @RequestParam("databaseId") Long databaseId) {
        Database database = databaseRepository.findById(databaseId).orElse(null);
        log.info("database id"+databaseId);
        if (databaseRepository.count() == 1){
            customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, principal.getName(), "Database "+database+"is the last database on server, cant be deleted "));
            return generateTableFragmentHtml(request, response, databaseRepository.findAllByOrderByName());
        }
        if (database != null) {
            List<UDRLink> UDRLinklist = udrLinkRepository.findAllByDatabase(database);
            udrLinkRepository.deleteAll(UDRLinklist);
            databaseRepository.delete(database);
            //TODO save confirm massage to admin (maybe email)
            if(databaseBackupService.performDatabaseBackup()){
                schemaService.deleteSchema(database.getName());
                File databasePath = new File(imagesFolder+ CurrentDatabase.getCurrentDatabase());
                if (databasePath.exists() && databasePath.isDirectory()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HHmmss_ddMMyyyy");
                    String currentTime = sdf.format(new Date());

                    File parentDirectory = databasePath.getParentFile();
                    String newFolderName = databasePath.getName() + "_backup_" + currentTime;

                    int maxNameLength = 255; //255 is Windows, Unix are often longer, so I hope this will be ok
                    if (newFolderName.length() > maxNameLength) {
                        newFolderName = newFolderName.substring(0, maxNameLength);
                    }

                    File newFolderPath = new File(parentDirectory, newFolderName);
                    if (databasePath.renameTo(newFolderPath)) {
                        System.out.println("Folder renamed successfully.");
                    } else {
                        System.err.println("Failed to rename folder.");
                    }
                } else {
                    System.err.println("The folder does not exist or is not a directory.");
                }
            }
            List<User> userList = UDRLinkService.findUsersInUDRList(UDRLinklist);
            for (User user : userList) {
                List<UDRLink> list = udrLinkRepository.findAllByUserName(user.getName());
                if (list != null){
                    userRepository.updateCurrentDB_name(database.getName(), list.get(1).getDatabase().getName());
                }else{
                    userRepository.updateCurrentDB_name(database.getName(), null);
                    customLoggerRepository.save(new LoggerLine(LogTyp.WARN, "deleteDatabase", "User "+ principal.getName() + " has no database"));
                }
            }
        }
        return generateTableFragmentHtml(request, response, databaseRepository.findAllByOrderByName());
    }
    private String generateTableFragmentHtml(HttpServletRequest request, HttpServletResponse response, List<Database> databaseList) {
        System.out.println("HOH2" + databaseList);
        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("databaseList", databaseList);
        return templateEngine.process("admin/databaseTable.html", context);
    }

    @PostMapping("/changeUserDatabase")
    @ResponseBody
    public String changeUserDatabase(HttpServletRequest request, HttpServletResponse response, Principal principal, @RequestParam("changeDBUserId") Long userId, @RequestParam("databaseName") String databaseName) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            customLoggerRepository.save(new LoggerLine(LogTyp.WARN, principal.getName(), "User is null"));
            return generateUserTable(request, response, userRepository.findAllByOrderByName());
        }
        if (!udrLinkRepository.findAllByUserNameAndDatabaseName(user.getName(), databaseName).isEmpty() || udrLinkService.isAdmin(user)) {
            if (databaseName.isBlank()){
                user.setCurrentDB_name(null);
            }else{
                user.setCurrentDB_name(databaseName);
            }
            userRepository.save(user);
            //userDetailsService.updateCustomUserDetails(user.getName());
            return generateUserTable(request, response, userRepository.findAllByOrderByName());
        }else{
            String message = "User "+user.getName()+" have not access to " + databaseName;
            customLoggerRepository.save(new LoggerLine(LogTyp.WARN, principal.getName(),message ));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                response.getWriter().write(message + databaseName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
    @PostMapping("/changeUserActive")
    @ResponseBody
    public String changeUserActive( Principal principal, @RequestParam("changeUserActiveId") Long userId, @RequestParam("active") Boolean active) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setActive(active);
            userRepository.save(user);
            return "OK";
        } else {
            customLoggerRepository.save(new LoggerLine(LogTyp.WARN, principal.getName(), "Activation was NOT changed."));
            return "Oh no!";
        }

    }

    @PostMapping("/addUDRLink")
    @ResponseBody
    public String addUDRLink(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestParam("user") Long userId,
                             @RequestParam("database") Long databaseId,
                             @RequestParam("role") Long roleId) {
        User user = userRepository.findById(userId).orElse(null);
        Role role = roleRepository.findById(roleId).orElse(null);
        if (user == null || role == null) {
            return generateUDRLinkTable(request, response);
        }
        Database database = null;
        if (!role.getName().equals("ADMIN")) {
            database = databaseRepository.findById(databaseId).orElse(null);
        }
        UDRLink udRlink = udrLinkRepository.findByUserAndDatabaseAndRole(user, database, role);
        if (udRlink != null) {
            customLoggerRepository.save(
                    new LoggerLine(LogTyp.ERROR,
                            "adminTools",
                            udRlink + " already exists"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                response.getWriter().write("UDRLink "+udRlink+" already exists");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        UDRLink udrlink = new UDRLink();
        udrlink.setUser(user);
        if (role.getName().equals("ADMIN")) {
            udrlink.setDatabase(null);
        }else{
            udrlink.setDatabase(database);
        }
        udrlink.setRole(role);
        udrLinkRepository.save(udrlink);
        //userDetailsService.updateCustomUserDetails(user.getName());

        return generateUDRLinkTable(request, response);
    }
    @PostMapping("/deleteUDRLink")
    @ResponseBody
    public String deleteUDR(Principal principal,
                            HttpServletResponse response,
                            @RequestParam("udrLinkId") Long udrLinkId) {
        if(udrLinkRepository.getById(udrLinkId).getRole().getName().equals("ADMIN")){
            customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, principal.getName(), "ADMIN cant be deleted from WEB"));
            try {
                response.getWriter().write("ADMIN cant be deleted from WEB");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
        udrLinkRepository.deleteById(udrLinkId);
        return "User deleted";
    }

    private String generateUDRLinkTable(HttpServletRequest request, HttpServletResponse response) {
        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("UDRLinkList", udrLinkRepository.findAllByOrderById());
        return templateEngine.process("admin/UDRLinkTable.html", context);
    }

    @PostMapping("/addUser")
    @ResponseBody
    public String addUser(Principal principal,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam(value = "active", defaultValue = "false") boolean active) {

        if (userRepository.findByName(username).isPresent()) {
            customLoggerRepository.save(new LoggerLine(LogTyp.WARN, principal.getName(), "User " + username +  " already exist"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                response.getWriter().write("User already exists");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setCurrentDB_name(null);
        user.setActive(active);
        userRepository.save(user);

        return generateUserTable(request, response, userRepository.findAllByOrderByName());
    }
    @PostMapping("/deleteUser")
    @ResponseBody
    @Transactional
    public String deleteUser(@RequestParam("userId") Long userId) {
        User user = userRepository.getById(userId);
        udrLinkRepository.deleteAllByUser(user);
        userFindRepository.deleteAllByUser(user);
        userRepository.deleteById(userId);
        return "User " + user.getName() + " deleted" ;
    }

    private String generateUserTable(HttpServletRequest request, HttpServletResponse response, List<User> userList) {
        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("userList", userList);
        context.setVariable("databaseList", databaseRepository.findAllByOrderByName());
        return templateEngine.process("admin/userTable.html", context);
    }

}

