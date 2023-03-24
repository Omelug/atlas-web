package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetaisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Skupina;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.TransportniObrazek;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.TransportniPolozka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class RestAPIController {
	private final AtlasService service;

	@GetMapping("/skupiny/{id}")
	public ResponseEntity<TransportniPolozka> skupina(@PathVariable("id") Integer id) {
		TransportniPolozka tp = service.polozkaToTP(id);
		return ResponseEntity.ok(tp);
		
	}

	@GetMapping("/flowersList")
	public ResponseEntity<List<TransportniPolozka>> odeslatDatabazi() {
		List<TransportniPolozka> database = new ArrayList<>();
		Skupina rootPolozka = service.najdiRootSkupinu();
		service.rekurzivniPridavaniPolozek(rootPolozka.getId(), database);
		return new ResponseEntity<List<TransportniPolozka>>(database, HttpStatus.OK);
	}
	@Autowired
	private CustomUserDetaisService userService;

	@GetMapping("/imagesList")
	public ResponseEntity<List<TransportniObrazek>> odeslatObrazky() {
		List<TransportniObrazek> obrazky = new ArrayList<>();
		service.pridavaniObrazku(obrazky);
		return new ResponseEntity<List<TransportniObrazek>>(obrazky, HttpStatus.OK);
	}
	@GetMapping("/image/{id}")
	public ResponseEntity<InputStreamResource> obrazek (@PathVariable("id") Integer id) throws IOException {
	    InputStream in = service.inputStream(id);
		return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_JPEG)
			    .body(new InputStreamResource(in));
	}
}
