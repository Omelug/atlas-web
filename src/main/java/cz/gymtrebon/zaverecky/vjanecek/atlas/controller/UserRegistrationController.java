package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.form.UserDto;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetails;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetaisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserRegistrationController {
    private CustomUserDetaisService userService;

    @PostMapping(value="/login-page/login", params="akce-register")
    public String registerUserAccount(Model model,
                                      @Valid @ModelAttribute("user") UserDto form,
                                      BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "login-page";
        }
        //userService.save(form);

        return "redirect:/home";
    }

    @GetMapping("/user")
    public String user() {
        return ("<h1>Welcome User</h1>");
    }

    @GetMapping(value = {"", "login-page"})
    public String login(Model model) {

        UserDto form = new UserDto();

        model.addAttribute("user", new UserDto());

        return "login-page";
    }

    @PostMapping(value="/login-page/login", params="akce-login")
    public String login(
            Model model,
            @Valid @ModelAttribute("user") UserDto form,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "login-page";
        }
        //TODO prihlaseni s form
        return "redirect:/home";
    }
}
