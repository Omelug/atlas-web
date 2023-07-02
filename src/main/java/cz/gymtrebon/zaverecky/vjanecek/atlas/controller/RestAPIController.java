package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Group;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.TransportImage;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.TransportItem;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetaisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
public class RestAPIController {
	private final AtlasService service;

	@GetMapping("/group/{id}")
	public ResponseEntity<TransportItem> group(@PathVariable("id") Integer id) {
		TransportItem tp = service.ItemToTP(id);
		return ResponseEntity.ok(tp);
	}

	@GetMapping("/itemList")
	public ResponseEntity<List<TransportItem>> odeslatDatabazi() {
		List<TransportItem> database = new ArrayList<>();
		Group rootItem = service.findORcreateGroup();
		service.rekurzivniPridavaniPolozek(rootItem.getId(), database);
		return new ResponseEntity<List<TransportItem>>(database, HttpStatus.OK);
	}
	@Autowired
	private CustomUserDetaisService userService;

	@GetMapping("/imageList")
	public ResponseEntity<List<TransportImage>> odeslatObrazky() {
		List<TransportImage> obrazky = new ArrayList<>();
		service.pridavaniObrazku(obrazky);
		return new ResponseEntity<List<TransportImage>>(obrazky, HttpStatus.OK);
	}
	@GetMapping("/image/{id}")
	public ResponseEntity<InputStreamResource> Image (@PathVariable("id") Integer id) throws IOException {
	    InputStream in = service.inputStream(id);
		return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_JPEG)
			    .body(new InputStreamResource(in));
	}
}
