package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Database;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.UDRlink;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UDRlinkRepository extends JpaRepository<UDRlink, Long> {
    List<UDRlink> findAllByUser(User user);
    List<UDRlink> findAllByDatabase(Database database);
    List<UDRlink> findAllByUserName(String userName);
    List<UDRlink> findAllByUserNameAndDatabaseName(String userName, String databaseName);
    void deleteById(Long udrlinkId);
}
