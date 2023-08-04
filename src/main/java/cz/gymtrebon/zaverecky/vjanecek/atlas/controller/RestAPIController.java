package cz.gymtrebon.zaverecky.vjanecek.atlas.controller;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Request;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.RequestImage;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.RequestImageRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.RequestRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRlinkRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('" + User.USER + "') OR hasAuthority('" + User.EDITOR + "') OR hasAuthority('" + User.ADMIN + "')")
public class RestAPIController {
	private final AtlasService service;
	private final UserRepository userRepository;
	private final UDRlinkRepository udrlinkRepository;
	private final CustomUserDetailsService userDetailsService;
	private final RequestRepository requestRepository;
	private final RequestImageRepository requestImageRepository;

	@GetMapping("/group/{id}")
	public ResponseEntity<TransportItem> group(@PathVariable("id") Integer id) {
		TransportItem tp = service.ItemToTransportItem(id);
		return ResponseEntity.ok(tp);
	}

	@GetMapping("/itemList")
	public ResponseEntity<List<TransportItem>> odeslatDatabazi() {
		List<TransportItem> database = new ArrayList<>();
		Group rootItem = service.findORcreateGroup();
		service.recursiveItemAdding(rootItem.getId(), database);
		return new ResponseEntity<>(database, HttpStatus.OK);
	}

	@GetMapping("/imageList")
	public ResponseEntity<List<TransportImage>> odeslatObrazky() {
		List<TransportImage> obrazky = new ArrayList<>();
		service.addImages(obrazky);
		return new ResponseEntity<>(obrazky, HttpStatus.OK);
	}
	@GetMapping("/image/{id}")
	public ResponseEntity<InputStreamResource> Image (@PathVariable("id") Integer id) throws IOException {
	    InputStream in = service.inputStream(id);
		return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_JPEG)
			    .body(new InputStreamResource(in));
	}
	@PostMapping("/changeDatabase")
	public ResponseEntity<String> changeDatabase(HttpServletRequest request, Authentication authentication) {
		User user = userRepository.findByName(authentication.getName()).orElse(null);
		String databaseName = request.getParameter("database");
		if (user != null && !udrlinkRepository.findAllByUserNameAndDatabaseName(authentication.getName(), databaseName).isEmpty()) {
			user.setCurrentDB_name(databaseName);
			userRepository.save(user);
			userDetailsService.updateCustomUserDetails(user.getName());
			log.info("Vše by mělo pracovat s " + CurrentDatabase.getCurrentDatabase());
		}
		return ResponseEntity.ok("Success");
	}
	@PostMapping("/request/imageTyp")
	public void handleTransportRequest(@RequestBody TransportRequest transportRequest) {
		System.out.println("Received TransportRequest: " + transportRequest.toString());
		if (requestRepository.findByRequestMark(transportRequest.getRequestMark()).isEmpty()) { //TODO tady aby se to zapisovalo do spravne database
			requestRepository.save(new Request(transportRequest));
		}

	}

	/**@PostMapping("/request/imageTyp/imageFile")
	public void handleimageFile(@RequestBody ) {
		System.out.println("Received TransportRequest: " + transportRequest.toString());
		if (requestRepository.findByRequestMark(transportRequest.getRequestMark()).isEmpty()) { //TODO tady aby se to zapisovalo do spravne database
			requestRepository.save(new Request(transportRequest));
		}

	}*/
	@PostMapping("/request/sendTransportRequestImages")
	public void handleTransportRequestImages(@RequestBody List<TransportRequestImage> transportRequestImages) {
		Optional<Request> request = requestRepository.findByRequestMark(transportRequestImages.get(0).getRequestMark());
		for (TransportRequestImage transportRequestImage : transportRequestImages){
			requestImageRepository.save(new RequestImage(transportRequestImage, request));
		}
	}
	@PostMapping("/request/sendImageFile")
	public void handleImageFile( @RequestPart("imageId") Integer imageId,@RequestPart("file") MultipartFile file) {
		//service.uploadImage(imageId, file);
		System.out.println("Priles file k obrazku "+ imageId);
		service.uploadRequestImage(imageId, file);
	}

}
