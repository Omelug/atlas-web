package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ColorRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.CustomLoggerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Statement;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchemaService {
    private final DataSource dataSource;
    //@Autowired
    //private MetadataSources metadataSources;
    //private final LocalContainerEntityManagerFactoryBean entityManagerFactory;
    private final EntityManagerFactory entityManagerFactory;
    @Value("${images.path}")
    private String imagesFolder;
    private final ColorRepository colorRepository;
    private final CustomLoggerRepository customLoggerRepository;
    public void updateSchema() {
        String currentDatabase = CurrentDatabase.getCurrentDatabase();
        if (currentDatabase != null) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();

            try {
                transaction.begin();

                Session session = entityManager.unwrap(Session.class);

                session.doWork(connection -> {
                    try (Statement statement = connection.createStatement()) {
                        int rowsAffected = statement.executeUpdate("SET search_path TO " + currentDatabase);
                        if (rowsAffected == 0) {
                            log.info("search_path set successfully to " + currentDatabase);
                        } else {
                            log.warn("Setting search_path did not affect any rows: " + rowsAffected);
                        }
                    }
                });
                log.info("Switched to schema " + CurrentDatabase.getCurrentDatabase());

                transaction.commit();
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                log.error("Error updating schema", e);
                CurrentDatabase.setCurrentDatabase(null);
            } finally {
                entityManager.close();
            }
        }
    }
    public void createSchema(String schemaName) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Session session = entityManager.unwrap(Session.class);

        session.doWork(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + schemaName);
            }
        });

        log.info("Schema created: " + schemaName);
        entityManager.close();
    }

    private boolean isValidSchemaName(String schemaName) {
        //TODO look oon the database name restriction around user iinput
        return true;
    }

    public void createUserSchema(String schemaName) {
        createSchema(schemaName);

        File folder = new File(imagesFolder+schemaName+"/images");
        if (!folder.exists()) {
            boolean folderCreated = folder.mkdirs();
            if (folderCreated) {
                //customLoggerRepository.save(new LoggerLine(LogTyp.INFO, "databaseCreation", "Folder for  "+schemaName+" created"));
            } else {
                //customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, "databaseCreation", "Failed to create the folder "+ folder.getAbsolutePath()));
            }
        } else {
            //customLoggerRepository.save(new LoggerLine(LogTyp.ERROR, "databaseCreation", "Folder for  "+schemaName+" already exists"));
        }

    }

    public void createTablesInSchema(String schemaName) {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect")
                //.applySetting("hibernate.hbm2ddl.auto", "validate")
                //.applySetting("hibernate.show_sql", "false")
                //.applySetting("hibernate.format_sql", "true")
                // Add other Hibernate properties here if needed
                .applySetting("hibernate.connection.datasource", dataSource)
                .applySetting("hibernate.default_schema", schemaName).build();

        MetadataSources sources = new MetadataSources(serviceRegistry);

        // Add the entity classes
        sources.addAnnotatedClass(Item.class);
        sources.addAnnotatedClass(Image.class);
        sources.addAnnotatedClass(Request.class);
        sources.addAnnotatedClass(RequestImage.class);
        sources.addAnnotatedClass(Color.class);

        Metadata metadata = sources.buildMetadata();

        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setHaltOnError(true);
        schemaExport.setFormat(true);
        schemaExport.setOutputFile("schema.sql");

        schemaExport.execute(EnumSet.of(TargetType.DATABASE), SchemaExport.Action.CREATE, metadata,
                serviceRegistry);

        log.info("Tables created in schema: " + schemaName);


    }

    public void createTablesInConfigSchema() {
        ServiceRegistry serviceRegistry = null;
        try {
            serviceRegistry = configureServiceRegistry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MetadataSources sources = new MetadataSources(serviceRegistry);

        sources.addAnnotatedClass(Database.class);
        //sources.addAnnotatedClass(AppSettings.class);
        sources.addAnnotatedClass(LoggerLine.class);
        sources.addAnnotatedClass(Role.class);
        sources.addAnnotatedClass(UDRLink.class);
        sources.addAnnotatedClass(User.class);
        sources.addAnnotatedClass(UserFind.class);

        Metadata metadata = sources.buildMetadata();


        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setHaltOnError(true);
        schemaExport.setFormat(true);
        schemaExport.setOutputFile("schema.sql");

        schemaExport.execute(EnumSet.of(TargetType.DATABASE),
                SchemaExport.Action.CREATE,
                metadata,
                serviceRegistry);

        log.info("Tables created in config schema: ");
    }

    private static ServiceRegistry configureServiceRegistry() throws IOException {
        Properties properties = getApplicationProperties();
        return new StandardServiceRegistryBuilder().applySettings(properties).build();
    }

    public static Properties getApplicationProperties() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
        properties.load(inputStream);

        Set<String> propertyNames = properties.stringPropertyNames();
        for (String propertyName : propertyNames) {
            String propertyValue = properties.getProperty(propertyName);
            System.out.println(propertyName + ": " + propertyValue);
        }

        return properties;
    }


    public void deleteSchema(String schemaName) {
        //EntityManager entityManager = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Session session = entityManager.unwrap(Session.class);

        session.doWork(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE");
            }
        });
        try {
            FileUtils.deleteDirectory(new File(imagesFolder+schemaName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Schema deleted: " + schemaName);
        entityManager.close();
    }
}