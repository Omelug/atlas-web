package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String username);
    @Transactional
    @Modifying
    @Query("UPDATE User r SET r.currentDB_name = :newDatabase WHERE r.currentDB_name = :oldDatabase")
    void updateDatabaseColumnToDefault(String oldDatabase, String newDatabase);
}
