package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Obrazek;

public interface ObrazekRepository extends JpaRepository<Obrazek, Integer> {
	
	
}
