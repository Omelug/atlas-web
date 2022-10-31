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
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Zastupce;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.SkupinaForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.ZastupceForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import lombok.RequiredArgsConstructor;
 
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AtlasController {

	private final AtlasService service;
	
	@GetMapping(value = {"", "home"})
	public String home(Model model) { 
		Skupina skupina = service.najdiRootSkupinu();
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
		
		model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());
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
 		model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());
 		
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
		
		model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());
		model.addAttribute("zastupce", form);
		return "zastupce-form";
	}

	@PostMapping(value="/zastupce/ulozit", params="akce-zpet")
	public String editaceSkupinyZpet(
			Model model,
			@ModelAttribute("zastupce") ZastupceForm form,
			BindingResult bindingResult) {
		return "redirect:/";
	} 

	@PostMapping(value="/zastupce/ulozit", params="akce-ulozit")
	public String editaceSkupinyUlozit(
			Model model,
			@Valid @ModelAttribute("zastupce") ZastupceForm form,
			BindingResult bindingResult) { 

 		boolean novyZakaznik = form.getId() == null;
 		model.addAttribute("nadrizeneSkupiny", service.seznamSkupin());
 		
		if (bindingResult.hasErrors()) {
			return "zastupce-form";
		}

		Integer idZastupce = null; 
		if (novyZakaznik) {
			idZastupce = service.vytvoritZastupce(
				form.getIdNadrizeneSkupiny(),	
				form.getNazev());
		} else {
			idZastupce = service.ulozZastupce(
				form.getIdNadrizeneSkupiny(),
				form.getId(),
				form.getNazev());
		}		
		return "redirect:/zastupce/" + idZastupce;
	}	
	
}
