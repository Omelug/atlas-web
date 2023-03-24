package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Polozka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;

public interface PolozkaRepository extends JpaRepository<Polozka, Integer> {
	Polozka findByTyp(Typ typ);
	List<Polozka> findAllByTyp(Typ typ);
	List<Polozka> findByNadrizenaSkupinaAndTyp(Polozka polozka, Typ typ);
	
}
