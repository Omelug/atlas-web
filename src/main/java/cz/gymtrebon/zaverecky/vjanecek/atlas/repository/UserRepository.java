package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.login.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
