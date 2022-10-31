package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Popisek;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Skupina;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Zastupce;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Polozka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.PolozkaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AtlasService {

	private final PolozkaRepository polozkaRepo;

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

	public Integer vytvoritSkupinu(
			Integer idNadrizeneSkupiny,
			String nazev) {
		
		Polozka nadrizena = polozkaRepo.getById(idNadrizeneSkupiny);
		
		Polozka p = new Polozka();
		p.setNazev(nazev);
		p.setTyp(Typ.SKUPINA);
		p.setNadrizenaSkupina(nadrizena);
		polozkaRepo.save(p);
		return p.getId();
	}

	public Integer ulozSkupinu(
			Integer idNadrizeneSkupiny,
			Integer idSkupiny,
			String nazev) {
		
		Polozka nadrizena = polozkaRepo.getById(idNadrizeneSkupiny);
		Polozka skupina = polozkaRepo.getById(idSkupiny);
		skupina.setNazev(nazev);
		skupina.setTyp(Typ.SKUPINA);
		skupina.setNadrizenaSkupina(nadrizena);
		polozkaRepo.save(skupina);
		return skupina.getId();
	}

	public Integer vytvoritZastupce(
			Integer idNadrizeneSkupiny,
			String nazev) {
		
		Polozka nadrizena = polozkaRepo.getById(idNadrizeneSkupiny);
		
		Polozka zastupce = new Polozka();
		zastupce.setNazev(nazev);
		zastupce.setTyp(Typ.ZASTUPCE);
		zastupce.setNadrizenaSkupina(nadrizena);
		polozkaRepo.save(zastupce);
		return zastupce.getId();
	}

	public Integer ulozZastupce(
			Integer idNadrizeneSkupiny,
			Integer idSkupiny,
			String nazev) {
		
		Polozka nadrizena = polozkaRepo.getById(idNadrizeneSkupiny);
		Polozka zastupce = polozkaRepo.getById(idSkupiny);
		zastupce.setNazev(nazev);
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
		if (polozka.getNadrizenaSkupina() != null) {
			zastupce.setIdNadrizeneSkupiny(polozka.getNadrizenaSkupina().getId());
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
}
