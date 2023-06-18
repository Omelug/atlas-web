package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.sql.Statement;

@Service
@Slf4j
public class SchemaService {

    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;

    public SchemaService(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void updateSchema() {
        EntityManager entityManager = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Session session = entityManager.unwrap(Session.class);

        session.doWork(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("SET search_path TO " + CurrentDatabase.getCurrentDatabase());
            }
        });
        log.info("Switched to schema " + CurrentDatabase.getCurrentDatabase());
        entityManager.close();
    }
}