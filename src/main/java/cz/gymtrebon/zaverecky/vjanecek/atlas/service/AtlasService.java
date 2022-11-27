package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Fotka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Popisek;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Skupina;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Zastupce;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Obrazek;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Polozka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ObrazekRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.PolozkaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtlasService {

	private final PolozkaRepository polozkaRepo;
	
	private final ObrazekRepository obrazekRepo;

	@Value( "${images.path}" )
	private String cestaKObrazkum;
	
	public Skupina najdiRootSkupinu() {
		return polozkaToSkupina(polozkaRepo.findByTyp(Typ.ROOT));
	}

	public List<Popisek> seznamSkupin() {
		List<Popisek> skupiny = new ArrayList<>();
		skupiny.add(polozkaToPopisek(polozkaRepo.findByTyp(Typ.ROOT)));
		for (Polozka polozka : polozkaRepo.findAllByTyp(Typ.SKUPINA)) {
			Popisek p = new Popisek();
			p.setId(polozka.getId());
			p.setNazev(polozka.getNazev());
			skupiny.add(p);
		}
		return skupiny;
	}

	public List<Skupina> seznamPodskupin(Integer idNadrizene) {
		Polozka nadrizena = polozkaRepo.getById(idNadrizene);
		List<Skupina> skupiny = new ArrayList<>();
		for (Polozka polozka : polozkaRepo.findByNadrizenaSkupinaAndTyp(nadrizena, Typ.SKUPINA)) {
			skupiny.add(polozkaToSkupina(polozka));
		}
		return skupiny;
	}

	public List<Zastupce> seznamZastupcu(Integer idNadrizene) {
		Polozka nadrizena = polozkaRepo.getById(idNadrizene);
		List<Zastupce> zastupce = new ArrayList<>();
		for (Polozka polozka : polozkaRepo.findByNadrizenaSkupinaAndTyp(nadrizena, Typ.ZASTUPCE)) {
			zastupce.add(polozkaToZastupce(polozka));
		}
		return zastupce;
	}
	public Integer vytvoritSkupinu(Integer idNadrizeneSkupiny, String nazev, String text) {

		Polozka nadrizena = polozkaRepo.getById(idNadrizeneSkupiny);

		Polozka p = new Polozka();
		p.setNazev(nazev);
		p.setText(text);
		p.setTyp(Typ.SKUPINA);
		p.setNadrizenaSkupina(nadrizena);
		polozkaRepo.save(p);
		return p.getId();
	}
 
	public Integer ulozSkupinu(Integer idNadrizeneSkupiny, Integer idSkupiny, String nazev, String text) {

		Polozka nadrizena = polozkaRepo.getById(idNadrizeneSkupiny);
		Polozka skupina = polozkaRepo.getById(idSkupiny);
		skupina.setNazev(nazev);
		skupina.setText(text);
		skupina.setTyp(Typ.SKUPINA);
		skupina.setNadrizenaSkupina(nadrizena);
		polozkaRepo.save(skupina);
		return skupina.getId();
	}
	public void smazatSkupinu(Integer skupinaId) {
		List<Skupina> podskupiny = seznamPodskupin(skupinaId);
		for (Skupina skupina : podskupiny) {
			smazatPolozku(skupina.getId());
		}
		List<Zastupce> zastupci = seznamZastupcu(skupinaId);
		for (Zastupce zastupce : zastupci) {
			smazatPolozku(zastupce.getId());
		}
		smazatPolozku(skupinaId);
	}
	public void smazatPolozku(Integer zastupceId) {
		if (!(polozkaRepo.getById(zastupceId).getTyp() == Typ.ROOT)) {
			polozkaRepo.deleteById(zastupceId);	
		}
	}

	public Integer vytvoritZastupce(Integer idNadrizeneSkupiny, String nazev, String nazev2, String autor, String barvy,
			String text) {

		Polozka nadrizena = polozkaRepo.getById(idNadrizeneSkupiny);

		Polozka zastupce = new Polozka();
		zastupce.setNazev(nazev);
		zastupce.setNazev2(nazev2);
		zastupce.setAutor(autor);
		zastupce.setBarvy(barvy);
		zastupce.setText(text);

		zastupce.setTyp(Typ.ZASTUPCE);
		zastupce.setNadrizenaSkupina(nadrizena);
		polozkaRepo.save(zastupce);
		return zastupce.getId();
	}

	public Integer ulozZastupce(Integer idNadrizeneSkupiny, Integer idSkupiny, String nazev, String nazev2,
			String autor, String barvy, String text) {

		Polozka nadrizena = polozkaRepo.getById(idNadrizeneSkupiny);
		Polozka zastupce = polozkaRepo.getById(idSkupiny);
		zastupce.setNazev(nazev);
		zastupce.setNazev2(nazev2);
		zastupce.setAutor(autor);
		zastupce.setBarvy(barvy);
		zastupce.setText(text);

		zastupce.setTyp(Typ.ZASTUPCE);
		zastupce.setNadrizenaSkupina(nadrizena);
		polozkaRepo.save(zastupce);
		return zastupce.getId();
	}

	public Skupina najdiSkupinuDleId(Integer idSkupiny) {
		Polozka polozka = polozkaRepo.getById(idSkupiny);
		return polozkaToSkupina(polozka);
	}

	public Zastupce najdiZastupceDleId(Integer idZastupce) {
		Polozka polozka = polozkaRepo.getById(idZastupce);
		return polozkaToZastupce(polozka);
	}

	public Skupina polozkaToSkupina(Polozka polozka) {
		Skupina s = new Skupina();
		s.setId(polozka.getId());
		s.setNazev(polozka.getNazev());
		s.setTextSkupiny(polozka.getText());
		if (polozka.getNadrizenaSkupina() != null) {
			s.setIdNadrizeneSkupiny(polozka.getNadrizenaSkupina().getId());
		}

		Polozka pom = polozka.getNadrizenaSkupina();
		while (pom != null && pom.getTyp() != Typ.ROOT) {
			Popisek b = new Popisek();
			b.setId(pom.getId());
			b.setNazev(pom.getNazev());
			s.getCesta().add(0, b);
			pom = pom.getNadrizenaSkupina();
		}

		return s;
	}

	public Zastupce polozkaToZastupce(Polozka polozka) {
		Zastupce zastupce = new Zastupce();
		zastupce.setId(polozka.getId());
		zastupce.setNazev(polozka.getNazev());
		zastupce.setNazev2(polozka.getNazev2());
		zastupce.setAutor(polozka.getAutor());
		zastupce.setBarvy(polozka.getBarvy());
		zastupce.setText(polozka.getText());
		if (polozka.getNadrizenaSkupina() != null) {
			zastupce.setIdNadrizeneSkupiny(polozka.getNadrizenaSkupina().getId());
		}
		if (polozka.getObrazky() != null) {
			for (Obrazek obrazek : polozka.getObrazky()) {
				Fotka f = new Fotka();
				f.setId(obrazek.getId());
				f.setNazev(obrazek.getJmenoSouboru());
				f.setUrl("/obrazek/" + obrazek.getId());
				zastupce.getFotky().add(f);
			}
		}
		Polozka pom = polozka.getNadrizenaSkupina();
		while (pom != null && pom.getTyp() != Typ.ROOT) {
			Popisek b = new Popisek();
			b.setId(pom.getId());
			b.setNazev(pom.getNazev());
			zastupce.getCesta().add(0, b);
			pom = pom.getNadrizenaSkupina();
		}

		return zastupce;
	}

	public Popisek polozkaToPopisek(Polozka polozka) {
		Popisek p = new Popisek();
		p.setId(polozka.getId());
		p.setNazev(polozka.getNazev());
		return p;
	}

	public void uploadObrazek(Integer polozkaId, MultipartFile file) {

		Polozka p = polozkaRepo.getById(polozkaId);
		
		Obrazek o = new Obrazek();
		o.setPolozka(p);
		o.setJmenoSouboru(file.getOriginalFilename());
		
		obrazekRepo.save(o);
		
		try {
			File f = new File(cestaKObrazkum, String.valueOf(o.getId())); 
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			log.error("Error while saving image", e);
			throw new RuntimeException("Error while saving image", e);
		}
		
	}
	
	public File souborObrazku(Integer obrazekId) {
		Obrazek o = obrazekRepo.getById(obrazekId);
		return new File(cestaKObrazkum, String.valueOf(o.getJmenoSouboru()));
	}

	public void deleteObrazek(Integer id, Integer obrazekId) {
		Obrazek o = obrazekRepo.getById(obrazekId);
		
		File f = new File(cestaKObrazkum, String.valueOf(o.getId())); 
		f.delete();
		
		obrazekRepo.delete(o);
		
	}
	
}
