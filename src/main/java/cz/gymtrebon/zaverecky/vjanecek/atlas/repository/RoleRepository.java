package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
