package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Group;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Representative;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.helpobjects.DatabaseListHelper;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Image;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.UDRlink;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.TestForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.DatabaseRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.RoleRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRlinkRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetaisService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j

public class AtlasController {

	private final AtlasService service;
	private static String text = "";
	private final UDRlinkRepository udrlinkRepository;
	private final UserRepository userRepository;
	private final DatabaseRepository databaseRepository;
	private final RoleRepository roleRepository;
	private final SchemaService schemaService;

	@Autowired
	CustomUserDetaisService userDetailsService;

	@PersistenceContext
	private EntityManager entityManager;

	@GetMapping(value = { "","test"})
	public String test(Model model) {
			TestForm form = new TestForm();
			form.setCelyText(text);
			model.addAttribute("text", form);
			return "test";

	}
	@PostMapping(value="/test2", params="action-test")
	public String test(
			@ModelAttribute("test") TestForm form) {
		text = form.getCelyText();
		return "redirect:/test";
	} 
	
	@GetMapping(value = {"/home"})
	public String home(Principal principal, Model model) {
		Group group = service.findORcreateGroup();
		model.addAttribute("home", true);
		return groupDetail(principal,model, group.getId());
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@GetMapping("/group/{id}")
	public String groupDetail(Principal principal,Model model,
								@PathVariable("id") Integer groupId) {
		
		Group group = service.najdiSkupinuDleId(groupId);
		model.addAttribute("group", group);
		model.addAttribute("subgroups", service.seznamPodskupin(group.getId()));
		model.addAttribute("representatives", service.seznamZastupcu(group.getId()));

		addDatabaseList(principal, model, udrlinkRepository);
		return "group-detail";
	}
	@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@GetMapping("/representative/{id}")
	public String detailRepresentative(Principal principal,Model model, @PathVariable("id") Integer representativeId) {
		Representative representative = service.najdiRepresentativeDleId(representativeId);
		model.addAttribute("representative", representative);

		addDatabaseList(principal, model, udrlinkRepository);
		return "representative-detail";
	}
	@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@GetMapping("/image/{id}")
	public ResponseEntity<InputStreamResource> Image(@PathVariable("id") Integer id) throws IOException {
		Image obr = service.najdiImageDleId(id);
		File file = service.souborObrazku(id);
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
	public String changeDatabase(Principal principal, HttpServletRequest request, @RequestParam("databaseName") String databaseName) {
		User user = userRepository.findByName(principal.getName()).orElse(null);
		if (user != null && !udrlinkRepository.findAllByUserNameAndDatabaseName(principal.getName(), databaseName).isEmpty()) {
			user.setCurrentDB_name(databaseName);
			userRepository.save(user);
			userDetailsService.updateCustomUserDetails(user.getName()); //TODO ykontrolovat
			log.info("Vše by mělo pracovat s " + CurrentDatabase.getCurrentDatabase());
		}
		return "redirect:/home";
		//String referer = request.getHeader("Referer");
		//return "redirect:" + referer;
	}
	public static void addDatabaseList(Principal principal, Model model, UDRlinkRepository udrlinkRepository) {
		String username = principal.getName();
		List<UDRlink> list = udrlinkRepository.findAllByUserName(username);
		List<DatabaseListHelper> databaseList = new ArrayList<DatabaseListHelper>();
		for (UDRlink udrlink : list) {
			boolean found = false;
			for (DatabaseListHelper dbhelper: databaseList ) {
				if (dbhelper.getDatabase().equals(udrlink.getDatabase().getName())) {
					dbhelper.addRole(udrlink.getRole().getName());
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

}
