package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Database;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Role;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.UDRlink;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.DatabaseRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.RoleRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRlinkRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('" + User.ADMIN + "')")
public class AdminController {
    private final UDRlinkRepository udRlinkRepository;
    private final UserRepository userRepository;
    private final DatabaseRepository databaseRepository;
    private final RoleRepository roleRepository;

    @GetMapping("")
    public String adminTools(Model model) {
        List<User> userList = userRepository.findAll();
        model.addAttribute("userList", userList);

        List<Database> databaseList = databaseRepository.findAll();
        model.addAttribute("databaseList", databaseList);

        List<Role> roleList = roleRepository.findAll();
        model.addAttribute("roleList", roleList);

        List<UDRlink> udrlinkList = udRlinkRepository.findAll();
        model.addAttribute("udrlinkList", udrlinkList);

        return "admin";
    }

    @PostMapping("/delete")
    public String deleteDatabase(@RequestParam("databaseId") Long databaseId) {
        Database database = databaseRepository.findById(databaseId).orElse(null);
        log.info("database id"+databaseId);
        if (database != null) {
            List<UDRlink> udrlinks = udRlinkRepository.findAllByDatabase(database);
            udRlinkRepository.deleteAll(udrlinks);
            databaseRepository.delete(database);
            //TODO delete remove schema and directories from web and save confirm massage to admin (maybe email)
        }
        return "redirect:/admin";
    }
    @PostMapping("/add")
    public String addDatabase(@RequestParam("databaseName") String databaseName) {
        if (databaseName.isBlank()) {
            return "redirect:/admin";
        }
        if (databaseRepository.findByName(databaseName).isPresent()) {
            //TODo error that databse already exist
            return "redirect:/admin";
        }
        log.info("database name "+databaseName);
        Database database = new Database();
        database.setName(databaseName);
        databaseRepository.save(database);
        //create schema for dataabase
        //TODO create schema new SchamaCreateor().createSchema(databaseName);
        return "redirect:/admin";
    }
    @PostMapping("/addUDRlink")
    public String addUDRlink(@RequestParam("user") Long userId, @RequestParam("database") Long databaseId, @RequestParam("role") Long roleId) {
        User user = userRepository.findById(userId).orElseThrow();
        Database database = databaseRepository.findById(databaseId).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();

        UDRlink udrlink = new UDRlink();
        udrlink.setUser(user);
        udrlink.setDatabase(database);
        udrlink.setRole(role);

        udRlinkRepository.save(udrlink);

        return "redirect:/admin";
    }
    @PostMapping("/deleteUDR")
    public String deleteUDR(@RequestParam("udrlinkId") Long udrlinkId) {
        udRlinkRepository.deleteById(udrlinkId);
        return "redirect:/admin";
    }
    @PostMapping("/addUser")
    public String addUser(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam(value = "active", defaultValue = "false") boolean active) {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setCurrentDB_name("public");
        user.setActive(active);
        userRepository.save(user);

        return "redirect:/admin";
    }
    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("userId") Long userId) {
        userRepository.deleteById(userId);
        return "redirect:/admin";
    }

}
