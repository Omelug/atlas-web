package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Skupina;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Zastupce;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Obrazek;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.SkupinaForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.TestForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.ZastupceForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class AtlasController {

	private final AtlasService service;
	private static String text = "";
	@PreAuthorize("hasAnyAuthority('USER')")
	@GetMapping(value = { "","test"})
	public String test(HttpServletRequest request, Model model) {
			TestForm form = new TestForm();
			form.setCelyText(text);
			model.addAttribute("text", form);
			return "test";

	}
	@PostMapping(value="/test2", params="akce-test")
	public String test(
			Model model,
			@ModelAttribute("test") TestForm form) {
		text = form.getCelyText();
		return "test";
	} 
	
	@GetMapping(value = {"/home"})
	public String home(Model model) {
		Skupina skupina = service.najdiRootSkupinu();
		model.addAttribute("home", true);
		return detailSkupiny(model, skupina.getId());
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/skupina/{id}")
	public String detailSkupiny(
			Model model,
			@PathVariable("id") Integer skupinaId) { 
		
		Skupina skupina = service.najdiSkupinuDleId(skupinaId);
		model.addAttribute("skupina", skupina);
		model.addAttribute("podskupiny", service.seznamPodskupin(skupina.getId()));
		model.addAttribute("zastupci", service.seznamZastupcu(skupina.getId()));
		
		return "skupina-detail";
	}

	@GetMapping("/skupina/nova")
	public String novaSkupina(
			@RequestParam("idNadrizeneSkupiny") Optional<Integer> idNadrizeneSkupiny, 
			Model model) {
		SkupinaForm form = new SkupinaForm();
		if (idNadrizeneSkupiny.isPresent()) {
			form.setIdNadrizeneSkupiny(idNadrizeneSkupiny.get());	
		}
		model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());
		model.addAttribute("skupina", form);
		model.addAttribute("nova", true);
		return "skupina-form";
	}
	@PreAuthorize("hasAnyAuthority('EDITOR')")
	@GetMapping("/skupina/{id}/editovat")
	public String editovatSkupinu(
			@PathVariable("id") Integer id, 
			Model model) {
		
		Skupina skupina = service.najdiSkupinuDleId(id);
		SkupinaForm form = new SkupinaForm();
		form.setId(skupina.getId());
		form.setIdNadrizeneSkupiny(skupina.getIdNadrizeneSkupiny());	
		form.setNazev(skupina.getNazev());
		form.setTextSkupiny(skupina.getTextSkupiny());
		
		model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());
		model.addAttribute("skupina", form);
		model.addAttribute("nova", false);
		return "skupina-form";
	}
	//rekurzivni
	@PreAuthorize("hasAnyAuthority('EDITOR')")
	@PostMapping(value="/skupina/ulozit", params="akce-smazat")
	public String smazaniSkupiny(
			Model model,
			@ModelAttribute("skupina") SkupinaForm form,
			BindingResult bindingResult) {
		log.info("form id  je " + form.getId() );
		if (!(form.getId() == null)) {
			service.smazatSkupinu(form.getId());	
		}
		if ( form.getIdNadrizeneSkupiny() == null) {
			return home(model);
		}
		return "redirect:/skupina/" + form.getIdNadrizeneSkupiny();
	}
	@PostMapping(value="/skupina/ulozit", params="akce-zpet")
	public String editaceSkupinyZpet(
			Model model,
			@ModelAttribute("skupina") SkupinaForm form,
			BindingResult bindingResult) {
		boolean novaSkupina = form.getId() == null;
		if (novaSkupina) {
			return "redirect:/skupina/" + form.getIdNadrizeneSkupiny();
		}
		return "redirect:/skupina/" + form.getId();
	}
	@PreAuthorize("hasAnyAuthority('EDITOR')")
	@PostMapping(value="/skupina/ulozit", params="akce-ulozit")
	public String editaceSkupinyUlozit(
			Model model,
			@Valid @ModelAttribute("skupina") SkupinaForm form,
			BindingResult bindingResult) { 

 		boolean novaSkupina = form.getId() == null;
 		model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());
 		
		if (bindingResult.hasErrors()) {
			return "skupina-form";
		}

		Integer idSkupiny = null; 
		if (novaSkupina) {
			idSkupiny = service.vytvoritSkupinu(
				form.getIdNadrizeneSkupiny(),
				form.getNazev(),
				form.getTextSkupiny());
			
		} else {
			idSkupiny = service.ulozSkupinu(
				form.getIdNadrizeneSkupiny(),
				form.getId(),
				form.getNazev(),
				form.getTextSkupiny());
		}		
		return "redirect:/skupina/" + idSkupiny;
	}

	@GetMapping("/zastupce/{id}")
	public String detailZastupce(
			Model model,
			@PathVariable("id") Integer zastupceId) { 
		Zastupce zastupce = service.najdiZastupceDleId(zastupceId);
		model.addAttribute("zastupce", zastupce);
		return "zastupce-detail";
	}

	@PreAuthorize("hasAnyAuthority('EDITOR')")
	@GetMapping("/zastupce/novy")
	public String novyZastupce(
			@RequestParam("idNadrizeneSkupiny") Optional<Integer> idNadrizeneSkupiny,
			Model model) { 

		ZastupceForm form = new ZastupceForm();
		if (idNadrizeneSkupiny.isPresent()) {
			form.setIdNadrizeneSkupiny(idNadrizeneSkupiny.get());	
		}
		model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());
		model.addAttribute("zastupce", form);
		model.addAttribute("novy", true);
		
		return "zastupce-form";
	}
	@PreAuthorize("hasAnyAuthority('EDITOR')")
	@GetMapping("/zastupce/{id}/editovat")
	public String editovatZastupce(
			@PathVariable("id") Integer id, 
			Model model) {
		
		Zastupce zastupce = service.najdiZastupceDleId(id);
		ZastupceForm form = new ZastupceForm();
		form.setId(zastupce.getId());
		form.setIdNadrizeneSkupiny(zastupce.getIdNadrizeneSkupiny());	
		form.setNazev(zastupce.getNazev());
		form.setNazev2(zastupce.getNazev2());
		form.setAutor(zastupce.getAutor());
		form.setBarvy(zastupce.getBarvy());
		form.setText(zastupce.getText());
		form.setFotky(zastupce.getFotky());
		
		model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());
		model.addAttribute("zastupce", form);
		model.addAttribute("novy", false);
		
		return "zastupce-form";
	}
	@PreAuthorize("hasAnyAuthority('EDITOR')")
	@PostMapping(value="/zastupce/ulozit", params="akce-smazat")
	public String smazaniZastupce(
			Model model,
			@ModelAttribute("zastupce") ZastupceForm form,
			BindingResult bindingResult) {
		if (!(form.getId() == null)) {
			service.smazatPolozku(form.getId());	
		}
		
		return "redirect:/skupina/" + form.getIdNadrizeneSkupiny();
	}
	@PreAuthorize("hasAnyAuthority('EDITOR')")
	@PostMapping(value="/zastupce/ulozit", params="akce-zpet")
	public String editaceZastupceZpet(
			Model model,
			@ModelAttribute("zastupce") ZastupceForm form,
			BindingResult bindingResult) {
		boolean novyZastupce = form.getId() == null;
		if (novyZastupce) {
			return "redirect:/"+ form.getIdNadrizeneSkupiny();
		}
		return "redirect:/zastupce/" + form.getId();
	}

	@PreAuthorize("hasAnyAuthority('EDITOR')")
	@PostMapping(value="/zastupce/ulozit", params="akce-ulozit")
	public String editaceZastupceUlozit(
			Model model,
			@Valid @ModelAttribute("zastupce") ZastupceForm form,
			BindingResult bindingResult) { 

 		boolean novyZastupce = form.getId() == null;
 		model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());
 		
		if (bindingResult.hasErrors()) {
			return "zastupce-form";
		}

		Integer idZastupce = null; 
		if (novyZastupce) {
			idZastupce = service.vytvoritZastupce(
				form.getIdNadrizeneSkupiny(),	
				form.getNazev(),
				form.getNazev2(),
				form.getAutor(),
				form.getBarvy(),
				form.getText()
				);
		} else {
			idZastupce = service.ulozZastupce(
				form.getIdNadrizeneSkupiny(),
				form.getId(),
				form.getNazev(),
				form.getNazev2(),
				form.getAutor(),
				form.getBarvy(),
				form.getText()
				);
		}		
		return "redirect:/zastupce/" + idZastupce;
	}
	@PreAuthorize("hasAnyAuthority('EDITOR')")
	@PostMapping("/zastupce/{polozkaId}/upload")
	public String uploadImage(
			@RequestParam("file") MultipartFile file, 
			@PathVariable("polozkaId") Integer polozkaId, 
			ModelMap modelMap) throws IOException {
		service.uploadObrazek(polozkaId, file);
		
		return "redirect:/zastupce/" + polozkaId;	    
	}
	@PreAuthorize("hasAnyAuthority('EDITOR')")
	@GetMapping("/zastupce/{id}/delete/{obrazekid}")
	public String deleteImage(
			@PathVariable("id") Integer id,
			@PathVariable("obrazekid") Integer obrazekid,
			ModelMap modelMap) throws IOException {

		service.deleteObrazek(id, obrazekid);
		
		return "redirect:/zastupce/" + id;	    
	}

	@GetMapping("/obrazek/{id}")
	public ResponseEntity<InputStreamResource> obrazek(@PathVariable("id") Integer id) throws IOException {
		Obrazek obr = service.najdiObrazekDleId(id);
		File file = service.souborObrazku(id);
		MediaType contentType = MediaType.IMAGE_PNG;
		String jmenoObrazku = obr.getJmenoSouboru().toString(); 
		if (jmenoObrazku.endsWith(".jpg") || jmenoObrazku.endsWith(".jpeg") ) {
			contentType = MediaType.IMAGE_JPEG;
		}
	    return ResponseEntity.ok()
    		.contentType(contentType)
    		.body(new InputStreamResource(new FileInputStream(file)));
	}

}
