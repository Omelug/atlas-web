package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Skupina;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.SkupinaForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import lombok.RequiredArgsConstructor;
 
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AtlasController {

	private final AtlasService service;
	
	@GetMapping(value = {"", "home"})
	public String home(Model model) { 
		return "home";
	}

	@GetMapping("/zastupce/novy")
	public String novyZastupce(Model model) { 
		return "zastupce-form";
	}

	@GetMapping("/skupina/{id}")
	public String detailSkupiny(
			Model model,
			@PathVariable("id") Integer skupinaId) { 
		
		Skupina skupina = service.najdiSkupinuDleId(skupinaId);
		model.addAttribute("skupina", skupina);
		
		return "skupina-detail";
	}

	@GetMapping("/skupina/nova")
	public String novaSkupina(Model model) {
		SkupinaForm form = new SkupinaForm();
		form.setIdNadrizeneSkupiny(service.najdiRootSkupnu().getId());
		model.addAttribute("skupina", form);
		return "skupina-form";
	}

	@PostMapping(value="/skupina/ulozit", params="akce-zpet")
	public String editaceSkupinyZpet(
			Model model,
			@ModelAttribute("skupina") SkupinaForm form,
			BindingResult bindingResult) {
		return "redirect:/";
	} 

	@PostMapping(value="/skupina/ulozit", params="akce-ulozit")
	public String editaceSkupinyUlozit(
			Model model,
			@Valid @ModelAttribute("skupina") SkupinaForm form,
			BindingResult bindingResult) { 

		boolean novyZakaznik = form.getId() == null;
		
		if (bindingResult.hasErrors()) {
			return "skupina-form";
		}

		Integer idSkupiny = null; 
		if (novyZakaznik) {
			idSkupiny = service.vytvoritSkupinu(
				form.getIdNadrizeneSkupiny(),	
				form.getNazev());
		} else {
			idSkupiny = service.ulozSkupinu(
				form.getIdNadrizeneSkupiny(),
				form.getId(),
				form.getNazev());
		}		
		return "redirect:/skupina/" + idSkupiny;
	}

	@GetMapping("/skupina/vyhledat")
	public String vybratSkupinu(Model model) { 
		return "skupina-pruzkumnik";
	}

}
