package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Group;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Representative;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Color;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Image;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.UserFind;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.DatabaseAccess;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.FindForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.RegistrationForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.TestForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j

public class AtlasController {
	private final ColorRepository colorRepository;

	private final AtlasService service;
	private static String text = "";
	private final UDRLinkRepository udrlinkRepository;
	private final UserRepository userRepository;
	private final FindService findService;
	private final UserFindRepository userFindRepository;
	private final CustomUserDetailsService userDetailsService;
	private final SpringTemplateEngine templateEngine;
	private final UDRLinkService udrLinkService;
	private final DatabaseRepository databaseRepository;
	private final UserService userService;
	private final UserFindService userFindService;
	@GetMapping(value = { ""})
	public String nothing() {
		return "redirect:/home";
	}

	@SuppressWarnings("SameReturnValue")
	@GetMapping(value = { "test"})
	public String test(Model model) {
			TestForm form = new TestForm();
			form.setAllText(text);
			model.addAttribute("text", form);
			return "test";

	}
	@SuppressWarnings("SameReturnValue")
	@PostMapping(value="/test2", params="action-test")
	public String test(
			@ModelAttribute("test") TestForm form) {
		text = form.getAllText();
		return "redirect:/test";
	}
	//@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@GetMapping(value = {"/home"})
	public String home(Principal principal, Model model) {
		if (principal == null) {
			return "without_account";
		}

		if (CurrentDatabase.getCurrentDatabase() == null) {
			model.addAttribute("publicDBList", databaseRepository.findAllByDatabaseAccessOrderByName(DatabaseAccess.PUBLIC));
			model.addAttribute("databaseIsSelected", false);
			return "without_database";
		}
		System.out.println("Curren " + CurrentDatabase.getCurrentDatabase());
		Group group = service.findORcreateGroup();
		model.addAttribute("home", true);
		return groupDetail(principal,model, group.getId());
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	@GetMapping("/loginUpdate")
	public String loginUpdate(Principal principal) {
		Optional<User> user = userRepository.findByName(principal.getName());
		if (user.isEmpty()) {
			return "redirect:/login";
		}
		userService.updateLastLogin(user.get());
		return "redirect:/home";
	}

	@GetMapping("/registration")
	public String registration(Model model) {
		model.addAttribute("registrationForm", new RegistrationForm());
		return "registration";
	}
	@PostMapping("/registration")
	public String processRegistration(
			@ModelAttribute("registrationForm") @Validated RegistrationForm registrationForm,
			BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			return "registration";
		}

		if (!registrationForm.getPassword().equals(registrationForm.getPassword2())) {
			model.addAttribute("passwordMismatch", true);
			return "registration";
		}

		if (userRepository.findByName(registrationForm.getUsername()).isEmpty()){
			User user = new User(registrationForm);
			userRepository.save(user);
			return "redirect:/login";
		}
		model.addAttribute("registrationError", true);
		return "redirect:/registration";
	}

	@SuppressWarnings("SameReturnValue")
	@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@GetMapping("/group/{id}")
	public String groupDetail(Principal principal,Model model,
								@PathVariable("id") Long groupId) {

		Group group = service.findGroupById(groupId);
		model.addAttribute("group", group);
		model.addAttribute("subgroups", service.subgroupList(group.getId()));
		model.addAttribute("representatives", service.representativeList(group.getId()));
		model.addAttribute("breadcrumbs", service.getBreadCrumbs(groupId));

		addBase(principal, model);

		return "group-detail";
	}
	@SuppressWarnings("SameReturnValue")
	@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@GetMapping("/representative/{id}")
	public String detailRepresentative(Principal principal,Model model, @PathVariable("id") Long representativeId) {
		Representative representative = service.findRepresentativeById(representativeId);
		model.addAttribute("representative", representative);

		model.addAttribute("breadcrumbs", service.getBreadCrumbs(representativeId));

		addBase(principal, model);

		return "representative-detail";
	}
	@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@GetMapping("/image/{id}")
	public ResponseEntity<InputStreamResource> Image(@PathVariable("id") Long id) throws IOException {
		Image obr = service.findImageById(id);
		File file = service.imageFile(id);
		MediaType contentType = MediaType.IMAGE_PNG;
		String imageName = obr.getFileName();
		if (imageName.endsWith(".jpg") || imageName.endsWith(".jpeg") ) {
			contentType = MediaType.IMAGE_JPEG;
		}
	    return ResponseEntity.ok()
    		.contentType(contentType)
    		.body(new InputStreamResource(new FileInputStream(file)));
	}

	@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@PostMapping("/changeDatabase")
	//@ResponseBody
	public String changeDatabase(HttpServletRequest request, HttpServletResponse response, Principal principal, @RequestParam("databaseName") String databaseName) {
		User user = userRepository.findByName(principal.getName()).orElse(null);
		if (user != null && !udrLinkService.getUDRLinks(user, databaseName).isEmpty()) {
			user.setCurrentDB_name(databaseName);
			userRepository.save(user);
			userDetailsService.updateCustomUserDetails(user.getName());
			log.info("Everything should work with " + CurrentDatabase.getCurrentDatabase());
		}
		//TODO muse se updatovat i detail itemu
		/*WebContext context = new WebContext(request, response, request.getServletContext());
		addDatabaseListContext(principal, context);
		System.out.println("awdaw" + templateEngine.process("layouts/foundItemTable.html", context));
		return templateEngine.process("layouts/toolbar_left.html", context);*/
		return "redirect:/home";
	}

	public void addDatabaseList(Principal principal, Model model) {
		if (principal != null) {
			model.addAttribute("databaseList", udrLinkService.getDatabaseList(principal.getName()));
			model.addAttribute("currentDatabase", CurrentDatabase.getCurrentDatabase());
		}
	}
	public void addDatabaseListContext(Principal principal, WebContext context) {
		context.setVariable("databaseList", udrLinkService.getDatabaseList(principal.getName()));
		context.setVariable("currentDatabase", CurrentDatabase.getCurrentDatabase());
	}
	public void addBase(Principal principal, Model model) {
		addDatabaseList(principal, model);
		UserFind u = userFindService.getUserFind(principal.getName());
		System.out.println("" + u.getName() + "");
		model.addAttribute("item", u);
		model.addAttribute("colorList", colorRepository.findAll());
		model.addAttribute("foundItems", findService.findItems(u));
		model.addAttribute("databaseIsSelected", true);
	}

	@PostMapping(value="/find")
	@ResponseBody
	public String find(HttpServletRequest request, HttpServletResponse response, Principal principal, @Valid @ModelAttribute("item") FindForm form) {

		boolean open = form.isOpen();
		String name = form.getName();
		String name2 = form.getName2();
		Typ typ = form.getTyp();
		String parentGroup = form.getParentGroup();
		String author = form.getAuthor();
		Set<Color> colors = form.getColors();
		String text = form.getText();

		Optional<UserFind> existingUserFind = userFindRepository.findByUserName(principal.getName());
		UserFind userFind = new UserFind();
		if (existingUserFind.isPresent()) {
			userFind = existingUserFind.get();
			userFind.setOpen(open);
			userFind.setName(name);
			userFind.setName2(name2);
			userFind.setTyp(typ);
			userFind.setParentGroup(parentGroup);
			userFind.setAuthor(author);
			userFind.setColors(colors);
			userFind.setText(text);
		}else{
			Optional<User> user = userRepository.findByName(principal.getName());
			if (user.isPresent()) {
				userFind = new UserFind(user.get(), name, name2, typ, parentGroup, author, colors, text, open);
				userFindRepository.save(userFind);
			}
		}

		WebContext context = new WebContext(request, response, request.getServletContext());
		//TODO searching
		context.setVariable("foundItems", findService.findItems(userFind));
		return templateEngine.process("layouts/foundItemTable.html", context);
	}

}
