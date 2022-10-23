package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Polozka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;

public interface PolozkaRepository extends JpaRepository<Polozka, Integer> {
	
	Polozka findByTyp(Typ typ);
	
}
