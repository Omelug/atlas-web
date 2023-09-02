package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
}