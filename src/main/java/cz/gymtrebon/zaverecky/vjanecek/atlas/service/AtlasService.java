package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import org.springframework.stereotype.Service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Breadcrumb;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Skupina;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Polozka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.PolozkaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AtlasService {

	private final PolozkaRepository polozkaRepo;

	public Skupina najdiRootSkupnu() {
		return polozkaToSkupina(polozkaRepo.findByTyp(Typ.ROOT));
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

	public Skupina najdiSkupinuDleId(Integer idSkupiny) {
		Polozka polozka = polozkaRepo.getById(idSkupiny);
		return polozkaToSkupina(polozka);
	}

	public Skupina polozkaToSkupina(Polozka polozka) {
		Skupina s = new Skupina();
		s.setId(polozka.getId());
		s.setNazev(polozka.getNazev());
		
		Polozka pom = polozka;
		while (pom.getTyp() != Typ.ROOT) {
			Breadcrumb b = new Breadcrumb();
			b.setId(pom.getId());
			b.setNazev(pom.getNazev());
			s.getCesta().add(0, b);
			pom = pom.getNadrizenaSkupina();
		}
		
		return s;
	}

}
