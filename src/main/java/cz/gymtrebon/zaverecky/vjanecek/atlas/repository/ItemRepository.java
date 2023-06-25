package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Item;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
	Optional<Item> findByTyp(Typ typ);
	List<Item> findAllByTyp(Typ typ);
	//List<Item> findByNadrizenaSkupinaAndTyp(Item Item, Typ typ);
	List<Item> findByParentGroupAndTyp(Item Item, Typ typ);
	List<Item> findAll(Specification<Item> specification);
	//List<Item> findByTypAndNameContainingAndName2ContainingAndAuthorContainingAndColorContainingAndTextContainingAndParentGroupContaining(Typ typ, String name, String name2, String author, String color, String text, String parentGroup);

    //@Query("SELECT new cz.gymtrebon.zaverecky.vjanecek.atlas.dto.BreadCrumb(i.id, i.name, i.parentGroup.id) FROM Item i WHERE i.id = :id AND i.name = :name AND i.parentGroup = :parentGroup")
	//Optional<BreadCrumb> findBreadCrumbById(@Param("id") Integer id);

	//@Query("SELECT new com.example.BreadCrumbDTO(i.id, i.name) FROM Item i WHERE i.parentGroup = :parentGroup AND i.typ = :typ")
	//List<BreadCrumb> findBreadCrumbsByParentGroupAndTyp(@Param("parentGroup") Item parentGroup, @Param("typ") Typ typ);
}
