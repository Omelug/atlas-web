package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Skupina;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.TransportniPolozka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Zastupce;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.SkupinaForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.TestForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.form.ZastupceForm;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import lombok.RequiredArgsConstructor;
 
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RestAPIController {

	private final AtlasService service;

	@GetMapping("/skupiny/{id}")
	public ResponseEntity<Skupina> obrazek(@PathVariable("id") Integer id) {
		TransportniPolozka root = service.findT();
		Skupina s = new Skupina();
		s.setId(id);
		s.setNazev("NAZEV");
		return ResponseEntity.ok(s);
	}

	@GetMapping("/skupiny")
	public ResponseEntity<TransportniPolozka> listPolozek() {
		TransportniPolozka root = service.findT();
		return ResponseEntity.ok(root);
	}

}
