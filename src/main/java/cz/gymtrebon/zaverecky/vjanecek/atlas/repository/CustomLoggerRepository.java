package cz.gymtrebon.zaverecky.vjanecek.atlas.repository;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.LoggerLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomLoggerRepository extends JpaRepository<LoggerLine, Long> {
    List<LoggerLine> findAll();
    List<LoggerLine> findAllByOrderByTimeDesc();
}
