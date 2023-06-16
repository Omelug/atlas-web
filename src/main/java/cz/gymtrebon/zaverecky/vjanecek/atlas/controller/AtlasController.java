package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Group;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Representative;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Image;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.TestForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRlinkRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j

public class AtlasController {

	private final AtlasService service;
	private static String text = "";
	private final UDRlinkRepository udRlinkRepository;

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
		return "test";
	} 
	
	@GetMapping(value = {"/home"})
	public String home(Principal principal, Model model) {
		Group group = service.najdiRootSkupinu();
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

		model.addAttribute("user_database", udRlinkRepository.findAllByUserName(principal.getName()) );
		return "group-detail";
	}
	@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
	@GetMapping("/representative/{id}")
	public String detailRepresentative(
			Model model,
			@PathVariable("id") Integer representativeId) {
		Representative representative = service.najdiRepresentativeDleId(representativeId);
		model.addAttribute("representative", representative);
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

}
