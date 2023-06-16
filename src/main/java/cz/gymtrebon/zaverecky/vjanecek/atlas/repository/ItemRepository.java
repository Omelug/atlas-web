package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Item;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
	Item findByTyp(Typ typ);
	List<Item> findAllByTyp(Typ typ);
	//List<Item> findByNadrizenaSkupinaAndTyp(Item Item, Typ typ);
	List<Item> findByParentGroupAndTyp(Item Item, Typ typ);
	
}
