package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Fotka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Popisek;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Skupina;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.TransportniObrazek;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.TransportniPolozka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Zastupce;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Obrazek;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Polozka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ObrazekRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.PolozkaRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtlasService {
	private final PolozkaRepository polozkaRepo;
	private final ObrazekRepository obrazekRepo;
	
	@Value("${images.path}")
	private String cestaKObrazkum;

	public Skupina najdiRootSkupinu() {
		List<Polozka> polozky = polozkaRepo.findAllByTyp(Typ.ROOT);
		for (Polozka polozka : polozky) {
			log.info("Polozka: " + polozka.getNazev());
		}

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
			smazatSkupinu(skupina.getId());
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

	public Obrazek najdiObrazekDleId(Integer id) {
		return obrazekRepo.getById(id);
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
				zastupce.getFotky().add(fotkazObrazku(obrazek));
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

	public Fotka fotkazObrazku(Obrazek obrazek) {
		Fotka f = new Fotka();
		f.setId(obrazek.getId());
		f.setNazev(obrazek.getJmenoSouboru());
		f.setUrl("/obrazek/" + obrazek.getId());
		return f;
	}

	public Popisek polozkaToPopisek(Polozka polozka) {
		Popisek p = new Popisek();
		p.setId(polozka.getId());
		p.setNazev(polozka.getNazev());
		return p;
	}

	public void uploadObrazek(Integer polozkaId, MultipartFile file) {

		Obrazek o = new Obrazek();
		o.setJmenoSouboru(file.getOriginalFilename());
		Polozka p = polozkaRepo.getById(polozkaId);
		o.setPolozka(p);
		obrazekRepo.save(o);
		log.info("Uploading file " + file.getOriginalFilename());
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

	public void uploadObrazek(Integer polozkaId, File file) {

		Polozka p = polozkaRepo.getById(polozkaId);

		Obrazek o = new Obrazek();
		o.setPolozka(p);
		o.setJmenoSouboru(file.getName());

		obrazekRepo.save(o);
		log.info("Importing file " + file.getAbsolutePath());
		try {
			File f = new File(cestaKObrazkum, String.valueOf(o.getId()));
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
			byte[] buffer = new byte[1024];
			int lengthRead;
			while ((lengthRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, lengthRead);
				out.flush();
			}
			out.close();
			in.close();
		} catch (IOException e) {
			log.error("Error while saving image", e);
			throw new RuntimeException("Error while saving image", e);
		}
	}

	public File souborObrazku(Integer obrazekId) {
		Obrazek o = obrazekRepo.getById(obrazekId);
		return new File(cestaKObrazkum, String.valueOf(o.getId()));
	}

	public void deleteObrazek(Integer id, Integer obrazekId) {
		Obrazek o = obrazekRepo.getById(obrazekId);

		File f = new File(cestaKObrazkum, String.valueOf(o.getId()));
		f.delete();

		obrazekRepo.delete(o);

	}

	// rekurzivni funkce pro zanoreni do podpolozek
	public TransportniPolozka polozkaToTP(Integer idPolozky) {
		TransportniPolozka tp = new TransportniPolozka();
		Polozka polozka = polozkaRepo.getById(idPolozky);

		tp.setNazev(polozka.getNazev());
		tp.setText(polozka.getText());
		tp.setTyp(polozka.getTyp());
		try {
			tp.setNadrizenaSkupinaid(polozka.getNadrizenaSkupina().getId());
		} catch (Exception e) {
			tp.setNadrizenaSkupinaid(0); //TODO  Tady je id pro hlavni slozku
			log.info("getNadrizenaSkupina je NULL");
		}

		tp.setId(polozka.getId());
		tp.setAutor(polozka.getAutor());
		tp.setBarvy(polozka.getBarvy());
		tp.setNazev2(polozka.getNazev2());

		return tp;
	}

	public void rekurzivniPridavaniPolozek(Integer idPolozky, List<TransportniPolozka> database) {
		pridatDoTransportniPolozka(idPolozky, database);
		for (Polozka podpolozka : polozkaRepo.getById(idPolozky).getPolozky()) {
			rekurzivniPridavaniPolozek(podpolozka.getId(), database);
		}
	}

	public void pridatDoTransportniPolozka(Integer idPolozky, List<TransportniPolozka> listTP) {
		TransportniPolozka tp = polozkaToTP(idPolozky);
		listTP.add(tp);
	}

	public void pridavaniObrazku(List<TransportniObrazek> obrazky) {
		List<Obrazek> obrazkyList = obrazekRepo.findAll();
		for (Obrazek obrazek : obrazkyList) {
			TransportniObrazek to = obrazekToTO(obrazek);
			obrazky.add(to);
		}
	}

	private TransportniObrazek obrazekToTO(Obrazek obrazek) {
		TransportniObrazek to = new TransportniObrazek();
		to.setId(obrazek.getId());
		to.setJmenoSouboru(obrazek.getJmenoSouboru());
		to.setPolozkaid(obrazek.getPolozka().getId());
		return to;
	}

	public InputStream inputStream(Integer obrazekId) throws FileNotFoundException {
		Obrazek o = obrazekRepo.getById(obrazekId);
		File f = new File(cestaKObrazkum, String.valueOf(o.getId()));
		InputStream vysledek = new FileInputStream(f);
		return vysledek;
	}

}
