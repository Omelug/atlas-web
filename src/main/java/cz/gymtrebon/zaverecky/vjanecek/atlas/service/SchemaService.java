package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Image;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Item;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.LoggerLine;
import cz.gymtrebon.zaverecky.vjanecek.atlas.log.LogTyp;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.CustomLoggerRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.io.File;
import java.sql.Statement;
import java.util.EnumSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchemaService {
    @Autowired
    private ItemRepository ItemRepo;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;
    @Value("${images.path}")
    private String imagesFolder;
    private final CustomLoggerRepository customLoggerRepository;

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
    public void createSchema(String schemaName) {
        EntityManager entityManager = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Session session = entityManager.unwrap(Session.class);

        session.doWork(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + schemaName);
            }
        });

        File folder = new File(imagesFolder+schemaName+"/images");

        if (!folder.exists()) {
            boolean folderCreated = folder.mkdirs();
            if (folderCreated) {
                customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, "databasecreation", "Folder for  "+schemaName+" created"));
            } else {
                customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, "databasecreation", "Failed to create the folder "+ folder.getAbsolutePath()));
            }
        } else {
            customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, "databasecreation", "Folder for  "+schemaName+" already exists"));
        }

        log.info("Schema created: " + schemaName);
        entityManager.close();
    }

    public void createTablesInSchema(String schemaName) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure()
                .applySetting("hibernate.default_schema", schemaName)
                .build();
        MetadataSources sources = new MetadataSources(registry);

        // Add the entity classes
        sources.addAnnotatedClass(Item.class);
        sources.addAnnotatedClass(Image.class);

        Metadata metadata = sources.buildMetadata();

        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setHaltOnError(true);
        schemaExport.setFormat(true);
        schemaExport.setOutputFile("schema.sql");

        schemaExport.execute(EnumSet.of(TargetType.DATABASE), SchemaExport.Action.CREATE, metadata,
                registry);

        log.info("Tables created in schema: " + schemaName);
    }

    public void deleteSchema(String schemaName) {
        EntityManager entityManager = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        Session session = entityManager.unwrap(Session.class);

        session.doWork(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE");
            }
        });

        File folder = new File(imagesFolder+schemaName+"/images");

        log.info("Schema deleted: " + schemaName);
        entityManager.close();
    }
}