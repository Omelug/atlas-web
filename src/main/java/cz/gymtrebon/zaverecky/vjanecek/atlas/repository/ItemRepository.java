package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Item;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	Optional<Item> findByTyp(Typ typ);
	List<Item> findAllByTyp(Typ typ);
	List<Item> findByParentGroupAndTyp(Item Item, Typ typ);
	List<Item> findAll(Specification<Item> specification);
}
