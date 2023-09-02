package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Database;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.DatabaseAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatabaseRepository extends JpaRepository<Database, Long> {
    Optional<Database> findByName(String name);
    List<Database> findByNameAndDatabaseAccess(String name, DatabaseAccess databaseAccess);
    List<Database> findAllByDatabaseAccessOrderByName(DatabaseAccess databaseAccess);
    @Override
    long count();
    List<Database> findAllByOrderByName();
}
