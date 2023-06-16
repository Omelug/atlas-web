package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Image;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
}
