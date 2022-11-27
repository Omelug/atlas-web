package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Skupina;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Zastupce;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Obrazek;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.SkupinaForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.TestForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.ZastupceForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ObrazekRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;

import lombok.RequiredArgsConstructor;
 
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AtlasController {

	private final AtlasService service;
	private static String text = "";
	@GetMapping(value = {"", "test"})
	public String test(Model model) {
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
		return "redirect:/test";
	} 
	
	@GetMapping(value = {"", "home"})
	public String home(Model model) { 
		Skupina skupina = service.najdiRootSkupinu();
		model.addAttribute("home", true);
		return detailSkupiny(model, skupina.getId());
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
	@PostMapping(value="/skupina/ulozit", params="akce-smazat")
	public String smazaniSkupiny(
			Model model,
			@ModelAttribute("skupina") SkupinaForm form,
			BindingResult bindingResult) {
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
		
		model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());
		model.addAttribute("zastupce", form);
		model.addAttribute("novy", false);
		
		return "zastupce-form";
	}
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
	
	@PostMapping("/zastupce/{id}/upload")
	public String uploadImage(
			@RequestParam("file") MultipartFile file, 
			@PathVariable("id") Integer id, 
			ModelMap modelMap) throws IOException {

		service.uploadObrazek(id, file);
		
		return "redirect:/zastupce/" + id;	    
	}
	@GetMapping("/zastupce/{id}/delete/{obrazekid}")
	public String deleteIamge(
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
		String path = obr.getJmenoSouboru().toString(); 
		if (path.endsWith(".jpg") || path.endsWith(".jpeg") ) {
			contentType = MediaType.IMAGE_JPEG;
		}
	    return ResponseEntity.ok()
    		.contentType(contentType)
    		.body(new InputStreamResource(new FileInputStream(file)));
	}

}
