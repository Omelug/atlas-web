package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Group;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Representative;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.helpobjects.DatabaseListHelper;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.FindForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.TestForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRlinkRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserFindRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetailsService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.FindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j

public class AtlasController {

	private final AtlasService service;
	private static String text = "";
	private final UDRlinkRepository udrlinkRepository;
	private final UserRepository userRepository;
	private final FindService findService;
	private final UserFindRepository userFindRepository;
	private final CustomUserDetailsService userDetailsService;

	@SuppressWarnings("SameReturnValue")
	@GetMapping(value = { "","test"})
	public String test(Model model) {
			TestForm form = new TestForm();
			form.setCelyText(text);
			model.addAttribute("text", form);
			return "test";

	}
	@SuppressWarnings("SameReturnValue")
	@PostMapping(value="/test2", params="action-test")
	public String test(
			@ModelAttribute("test") TestForm form) {
		text = form.getCelyText();
		return "redirect:/test";
	}
	@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@GetMapping(value = {"/home"})
	public String home(Principal principal, Model model) {
		Group group = service.findORcreateGroup();
		model.addAttribute("home", true);
		return groupDetail(principal,model, group.getId());
	}

	@SuppressWarnings("SameReturnValue")
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	@SuppressWarnings("SameReturnValue")
	@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@GetMapping("/group/{id}")
	public String groupDetail(Principal principal,Model model,
								@PathVariable("id") Integer groupId) {
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
	public String detailRepresentative(Principal principal,Model model, @PathVariable("id") Integer representativeId) {
		Representative representative = service.findRepresentativeById(representativeId);
		model.addAttribute("representative", representative);

		model.addAttribute("breadcrumbs", service.getBreadCrumbs(representativeId));

		addBase(principal, model);

		return "representative-detail";
	}
	@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@GetMapping("/image/{id}")
	public ResponseEntity<InputStreamResource> Image(@PathVariable("id") Integer id) throws IOException {
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
	public String changeDatabase(Principal principal, @RequestParam("databaseName") String databaseName) {
		User user = userRepository.findByName(principal.getName()).orElse(null);
		if (user != null && !udrlinkRepository.findAllByUserNameAndDatabaseName(principal.getName(), databaseName).isEmpty()) {
			user.setCurrentDB_name(databaseName);
			userRepository.save(user);
			userDetailsService.updateCustomUserDetails(user.getName());
			log.info("Everything should work with " + CurrentDatabase.getCurrentDatabase());
		}
		return "redirect:/home";
	}
	public void addDatabaseList(Principal principal, Model model) {
		String username = principal.getName();
		List<UDRlink> list = udrlinkRepository.findAllByUserName(username);
		List<DatabaseListHelper> databaseList = new ArrayList<>();
		for (UDRlink udrlink : list) {
			boolean found = false;
			for (DatabaseListHelper databaseListHelper: databaseList ) {
				if (databaseListHelper.getDatabase().equals(udrlink.getDatabase().getName())) {
					databaseListHelper.addRole(udrlink.getRole().getName());
					found = true;
				}
			}
			if (!found) {
				databaseList.add(new DatabaseListHelper(udrlink.getDatabase().getName(), udrlink.getRole().getName()));
			}
		}
		model.addAttribute("databaseList", databaseList);
		model.addAttribute("currentDatabase", CurrentDatabase.getCurrentDatabase());
	}
	public void addBase(Principal principal, Model model) {
		addDatabaseList(principal, model);
		Optional<UserFind> u = userFindRepository.findByUserName(principal.getName());
		if (u.isEmpty()) {
			UserFind userFind = new UserFind(userRepository.findByName(principal.getName()));
			userFindRepository.save(userFind);
		}
		model.addAttribute("item", userFindRepository.findByUserName(principal.getName()).orElse(new UserFind()));
		model.addAttribute("foundItems", findService.findItems(userFindRepository.findByUserName(principal.getName()).orElse(new UserFind())));
	}

	@PostMapping(value="/find")
	public String find(Principal principal, @Valid @ModelAttribute("item") FindForm form, HttpServletRequest request) {

		// Access the search criteria from the form object
		boolean open = form.isOpen();
		String name = form.getName();
		String name2 = form.getName2();
		Typ typ = form.getTyp();
		String parentGroup = form.getParentGroup();
		String author = form.getAuthor();
		String color = form.getColor();
		String text = form.getText();


		//save settings
		Optional<UserFind> existingUserFind = userFindRepository.findByUserName(principal.getName());
		UserFind userFind;
		if (existingUserFind.isPresent()) {

			userFind = existingUserFind.get();
			userFind.setOpen(open);
			userFind.setName(name);
			userFind.setName2(name2);
			userFind.setTyp(typ);
			userFind.setParentGroup(parentGroup);
			userFind.setAuthor(author);
			userFind.setColor(color);
			userFind.setText(text);
		}else{
			userFind = new UserFind(userRepository.findByName(principal.getName()), name, name2, typ, parentGroup, author, color, text, open);
		}
		userFindRepository.save(userFind);
		//TODO searching

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

}
