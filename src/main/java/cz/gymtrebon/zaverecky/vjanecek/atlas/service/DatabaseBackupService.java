package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DatabaseBackupService {

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Scheduled(cron = "0 0 0 */2 * ?") // Run every 2 days at midnight
    public boolean performDatabaseBackup() {
        //TODO sem se nekam musí dat heslo

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());
        String backupFileName = "backup_" + timestamp + ".sql";

        //String command = "pg_dump -U " + dbUser + " -f " + backupFileName + " -W " + dbPassword + " " + databaseUrl;

        // Parse the URL to extract components
        URI dbUri = null;
        try {
            dbUri = new URI(databaseUrl.replace("jdbc:", ""));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.err.println("Error: Invalid JDBC URL.");
        }

        //String dbUser = dbUri.getUserInfo();
        String host = dbUri.getHost();
        int port = dbUri.getPort();
        String dbName = dbUri.getPath().substring(1); // Remove the leading slash


        String command = "pg_dump -U "+ user+" -h "+host+" -p "+ port +" -d " + dbName+" > " + backupFileName;
        System.out.println(" BackUp with: " + command);


        try {

            Process process = Runtime.getRuntime().exec(command);

            OutputStream outputStream = process.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);
            writer.print("atlas");
            writer.flush();
            writer.close();

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Database backup completed successfully.");
                return true;
            } else {
                System.err.println("Error: Database backup failed.");
                return false;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error: Failed to execute the backup command.");
            return false;
        }
    }
}