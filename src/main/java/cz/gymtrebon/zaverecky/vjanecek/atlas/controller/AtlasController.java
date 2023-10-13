package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Group;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Representative;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.DatabaseAccess;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.FindForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.RegistrationForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.TestForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.log.LogTyp;
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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j

public class AtlasController {
	private final CustomLoggerRepository customLoggerRepository;
	private final ColorRepository colorRepository;

	private final AtlasService service;
	private static String text = "";
	private final UserRepository userRepository;
	private final FindService findService;
	private final UserFindRepository userFindRepository;
	private final CustomUserDetailsService userDetailsService;
	private final SpringTemplateEngine templateEngine;
	private final UDRLinkService udrLinkService;
	private final DatabaseRepository databaseRepository;
	private final UserService userService;
	private final ColorService colorService;
	private final UserFindService userFindService;
	private final ItemRepository itemRepository;
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
		Group root = service.findORcreateGroup();
		model.addAttribute("home", true);
		return itemDetail(principal,model, root.getId());
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
	@GetMapping("/item/{id}")
	public String itemDetail(Principal principal,Model model,
								@PathVariable("id") Long itemId) {
		Item item = itemRepository.getById(itemId);
		model.addAttribute("breadcrumbs", service.getBreadCrumbs(itemId));
		addBase(principal, model);
		if ((item.getTyp() == Typ.GROUP) || (item.getTyp() == Typ.ROOT)){
			Group group = service.ItemToGroup(item);
			model.addAttribute("group", group);
			model.addAttribute("subgroups", service.subgroupList(group.getId()));
			model.addAttribute("representatives", service.representativeList(group.getId()));
			return "group-detail";
		}
		if (item.getTyp() == Typ.REPRESENTATIVE){
			Representative representative = service.ItemToRepresentative(item);
			model.addAttribute("representative", representative);
			return "representative-detail";
		}
		customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, principal.getName(), "User try look on the item type " + item.getTyp()));
		return "errors/error404";
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
	public String changeDatabase(Principal principal, @RequestParam("databaseName") String databaseName) {
		User user = userRepository.findByName(principal.getName()).orElse(null);
		if (user != null && !udrLinkService.getUDRLinks(user, databaseName).isEmpty()) {
			user.setCurrentDB_name(databaseName);
			userRepository.save(user);
			userDetailsService.updateCustomUserDetails(user.getName());
			log.info("Everything should work with " + CurrentDatabase.getCurrentDatabase());
		}
		return "redirect:/home";
	}

	public void addDatabaseList(Principal principal, Model model) {
		if (principal != null) {
			model.addAttribute("databaseList", udrLinkService.getDatabaseList(principal.getName()));
			model.addAttribute("currentDatabase", CurrentDatabase.getCurrentDatabase());
		}
	}
	public void addBase(Principal principal, Model model) {
		addDatabaseList(principal, model);
		Optional<UserFind> userFind = userFindRepository.findByUserName(principal.getName());
		if (userFind.isPresent()){
			model.addAttribute("findForm", new FindForm(userFind.get())); //TODO errors
			model.addAttribute("foundItems", findService.findItems(userFind.get()));
		}else{
			model.addAttribute("findForm", new FindForm());
		}
		model.addAttribute("colorList", colorRepository.findAll());
		model.addAttribute("databaseIsSelected", true);
	}

	@PostMapping(value="/find")
	@ResponseBody
	public String find(@RequestParam(value = "colors", required = false) List<String> colors,
																				HttpServletRequest request, HttpServletResponse response,
																				Principal principal, @Valid @ModelAttribute("findForm") FindForm form) {

		boolean open = form.isOpen();
		String name = form.getName();
		String name2 = form.getName2();
		Typ typ = form.getTyp();
		String parentGroup = form.getParentGroup();
		String author = form.getAuthor();
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
			userFind.setColors(colorService.getColorsObject(colors));
			userFind.setText(text);
		}else{
			Optional<User> user = userRepository.findByName(principal.getName());
			if (user.isPresent()) {
				userFind = new UserFind(user.get(), name, name2, typ, parentGroup, author, null, text, open); //TODO nahradit null za colors
				userFindRepository.save(userFind);
			}
		}

		WebContext context = new WebContext(request, response, request.getServletContext());
		context.setVariable("foundItems", findService.findItems(userFind));
		return templateEngine.process("layouts/foundItemTable.html", context);
	}

}
