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
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('" + User.ADMIN + "')")
public class AdminController {
    private final UDRlinkRepository udRlinkRepository;
    private final UserRepository userRepository;
    private final DatabaseRepository databaseRepository;
    private final RoleRepository roleRepository;

    @GetMapping("admin")
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
    @PostMapping("/admin/delete")
    public String deleteDatabase(@RequestParam("databaseId") Long databaseId) {
        Database database = databaseRepository.findById(databaseId).orElse(null);
        log.info("database id"+databaseId);
        if (database != null) {
            List<UDRlink> udrlinks = udRlinkRepository.findAllByDatabase(database);
            udRlinkRepository.deleteAll(udrlinks);
            databaseRepository.delete(database);
        } databaseRepository.delete(database);
        return "redirect:/admin";
    }
    @PostMapping("/admin/add")
    public String addDatabase(@RequestParam("databaseName") String databaseName) {
        Database database = new Database();
        database.setName(databaseName);
        databaseRepository.save(database);
        return "redirect:/admin";
    }
    @PostMapping("/admin/addUDRlink")
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
    @PostMapping("/admin/deleteUDR")
    public String deleteUDR(@RequestParam("udrlinkId") Long udrlinkId) {
        udRlinkRepository.deleteById(udrlinkId);
        return "redirect:/admin";
    }
}
