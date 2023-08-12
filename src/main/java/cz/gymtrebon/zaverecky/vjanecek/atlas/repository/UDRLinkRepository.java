package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Database;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Role;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.UDRLink;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UDRLinkRepository extends JpaRepository<UDRLink, Long> {
    List<UDRLink> findAllByUser(User user);
    List<UDRLink> findAllByDatabase(Database database);
    List<UDRLink> findAllByUserName(String userName);
    List<UDRLink> findAllByUserNameAndDatabaseName(String userName, String databaseName);
    UDRLink findByUserAndDatabaseAndRole(User user,Database database, Role role);

    void deleteAllByUser(User user);
    void deleteById(Long UDRLinkId);
}
