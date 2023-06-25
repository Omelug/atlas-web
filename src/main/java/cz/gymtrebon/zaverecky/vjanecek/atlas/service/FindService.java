package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Item;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.UserFind;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ItemRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserFindRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FindService {

    private final UserFindRepository userFindRepository;
    private final ItemRepository itemRepository;

   /* public List<Item> findItems(UserFind userFind) {
        Typ typ = userFind.getTyp();
        String name = userFind.getName();
        String name2 = userFind.getName();
        String author = userFind.getAuthor();
        String color = userFind.getColor();
        String text = userFind.getText();
        String parentGroup = userFind.getParentGroup();

        //return itemRepository.findByTypAndNameContainingAndName2ContainingAndAuthorContainingAndColorContainingAndTextContainingAndParentGroupContaining(
        //        typ, name, name2, author, color, text, parentGroup);

        return itemRepository.findAll();
    }*/

    public List<Item> findItems(UserFind userFind) {
        Typ typ = userFind.getTyp();
        String name = userFind.getName();
        String name2 = userFind.getName2();
        String author = userFind.getAuthor();
        String color = userFind.getColor();
        String text = userFind.getText();
        String parentGroup = userFind.getParentGroup();
        log.info("UserFind "+name);
        // Use the repository to execute the dynamically generated query
        return itemRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (typ != null) {
                predicates.add(criteriaBuilder.equal(root.get("typ"), typ));
            }
            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (name2 != null && !name2.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name2")), "%" + name2.toLowerCase() + "%"));
            }
            if (author != null && !author.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + author.toLowerCase() + "%"));
            }
            if (color != null && !color.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("color")), "%" + color.toLowerCase() + "%"));
            }
            if (text != null && !text.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("text")), "%" + text.toLowerCase() + "%"));
            }
            if (parentGroup != null && !parentGroup.isEmpty()) {
                Join<Item, Item> parentGroupJoin = root.join("parentGroup");
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(parentGroupJoin.get("name")), "%" + parentGroup.toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
