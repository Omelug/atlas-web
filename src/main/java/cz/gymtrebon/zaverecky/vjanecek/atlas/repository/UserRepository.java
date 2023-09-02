package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String username);
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.currentDB_name = :newDatabase WHERE u.currentDB_name = :oldDatabase")
    void updateCurrentDB_name(String oldDatabase, String newDatabase);
    List<User> findAllByOrderByName();
}
