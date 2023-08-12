package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.log.LogTyp;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetailsService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
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
    private final CustomUserDetailsService userDetailsService;
    private final UserFindRepository userFindRepository;
    private final SpringTemplateEngine templateEngine;
    private final RequestRepository requestRepository;

    @GetMapping("")
    public String adminTools(Model model) {

        model.addAttribute("userList", userRepository.findAll());
        model.addAttribute("databaseList", databaseRepository.findAll());
        model.addAttribute("roleList", roleRepository.findAll());
        model.addAttribute("UDRLinkList", udrLinkRepository.findAll());
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
            return generateTableFragmentHtml(request, response, databaseRepository.findAll());
        }
        log.info("database name "+databaseName);
        Database database = new Database();
        database.setName(databaseName);
        databaseRepository.save(database);

        //create schema for dataabase
        schemaService.createUserSchema(databaseName);
        schemaService.createTablesInSchema(databaseName);
        String text = generateTableFragmentHtml(request, response, databaseRepository.findAll());
        return text;
    }
    @PostMapping("/deleteDatabase")
    @ResponseBody
    public String deleteDatabase(Principal principal, @RequestParam("databaseId") Long databaseId) {
        Database database = databaseRepository.findById(databaseId).orElse(null);
        log.info("database id"+databaseId);
        if (databaseRepository.count() != 1){
            customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, principal.getName(), "Database "+database+"is the last database on server, cant be deleted "));
            return "Database " + database+ " was NOT deleted";
        }
        if (database != null) {
            List<UDRLink> UDRLinklist = udrLinkRepository.findAllByDatabase(database);
            udrLinkRepository.deleteAll(UDRLinklist);
            databaseRepository.deleteById(database.getId());
            //TODO delete remove schema and directories from web and save confirm massage to admin (maybe email)
            schemaService.deleteSchema(database.getName());
            //projde spoje na databazi a nastavi default databazi
            userRepository.updateDatabaseColumnToDefault(database.getName(), CurrentDatabase.DEFAULT_DATABASE);
        }
        return "Database " + database+ " deleted";
    }
    private String generateTableFragmentHtml(HttpServletRequest request, HttpServletResponse response, List<Database> databaseList) {
        System.out.println("HOH2" + databaseList);
        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("databaseList", databaseList);
        return templateEngine.process("admin/databaseTable.html", context);
    }

    @PostMapping("/changeUserDatabase")
    @ResponseBody
    public String changeUserDatabase(Principal principal, @RequestParam("changeDBUserId") Long userId, @RequestParam("databaseName") String databaseName) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && !udrLinkRepository.findAllByUserNameAndDatabaseName(user.getName(), databaseName).isEmpty()) {
            user.setCurrentDB_name(databaseName);
            userRepository.save(user);
            userDetailsService.updateCustomUserDetails(user.getName());
            return "Database for user " +user.getName()+ " has been changed to " + databaseName;
        }else{
            customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, principal.getName(), "User "+user.getName()+" have not access to " + databaseName));
            return "User does not have access to the selected database";
        }
    }

    @PostMapping("/addUDRLink")
    @ResponseBody
    public String addUDRLink(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestParam("user") Long userId,
                             @RequestParam("database") Long databaseId,
                             @RequestParam("role") Long roleId) {
        User user = userRepository.findById(userId).orElseThrow();
        Database database = databaseRepository.findById(databaseId).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();
        UDRLink udRlink = udrLinkRepository.findByUserAndDatabaseAndRole(user, database, role);
        if (udRlink != null) {
            customLoggerRepository.save(
                    new LoggerLine(LogTyp.ERROR,
                            "adminTools",
                            udRlink + " already exists"));
            return generateUDRLinkTable(request, response);
        }

        UDRLink udrlink = new UDRLink();
        udrlink.setUser(user);
        if (role.equals("admin")) {
            udrlink.setDatabase(null);
        }else{
            udrlink.setDatabase(database);
        }
        udrlink.setRole(role);

        udrLinkRepository.save(udrlink);
        userDetailsService.updateCustomUserDetails(user.getName());

        return generateUDRLinkTable(request, response);
    }
    @PostMapping("/deleteUDRLink")
    @ResponseBody
    public String deleteUDR(Principal principal, @RequestParam("udrLinkId") Long udrLinkId) {
        if(udrLinkRepository.getById(udrLinkId).getRole().equals("ADMIN")){//TODO here should be maybe only warning
            customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, principal.getName(), "ADMIN cant be deleted from WEB"));
            return "ADMIN cant be deleted from WEB";
        }
        udrLinkRepository.deleteById(udrLinkId);
        return "User deleted";
    }

    private String generateUDRLinkTable(HttpServletRequest request, HttpServletResponse response) {
        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("UDRLinkList", udrLinkRepository.findAll());
        return templateEngine.process("admin/UDRLinkTable.html", context);
    }

    @PostMapping("/addUser")
    @ResponseBody
    public String addUser(HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam(value = "active", defaultValue = "false") boolean active) {

        //user aready exists?
        if (userRepository.findByName(username).isPresent()) {
            //TODO error that user already exists
            return "redirect:/admin";
        }
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setCurrentDB_name(CurrentDatabase.DEFAULT_DATABASE);
        user.setActive(active);
        userRepository.save(user);

        //String text = generateUserTable(request, response, userRepository.findAll());
        return generateUserTable(request, response, userRepository.findAll());
    }
    @PostMapping("/deleteUser")
    @ResponseBody
    public String deleteUser(HttpServletRequest request, HttpServletResponse response,@RequestParam("userId") Long userId) {
        User user = userRepository.getById(userId);
        udrLinkRepository.deleteAllByUser(user);
        userFindRepository.deleteAllByUser(user);
        userRepository.deleteById(userId);
        return "User " + user.getName() + " deleted" ;
    }

    private String generateUserTable(HttpServletRequest request, HttpServletResponse response, List<User> userList) {
        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("userList", userList);
        context.setVariable("databaseList", databaseRepository.findAll());
        return templateEngine.process("admin/userTable.html", context);
    }

}

