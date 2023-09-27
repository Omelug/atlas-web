package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DatabaseBackupService {

    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Value("${database.host}")
    private String host;
    @Value("${database.port}")
    private String port;
    @Value("${database.name}")
    private String databaseName;

    @Scheduled(cron = "0 0 0 */2 * ?") // Run every 2 days at midnight
    public void backUpTimer() {
        performDatabaseBackup();
    }
    public boolean performDatabaseBackup() {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss_ddMMyyyy");
        String currentTime = sdf.format(new Date());
        String backupFileName = databaseName+"_"+currentTime+".sql";

        String[] cmd = {
                "pg_dump",
                "--username",username,
                "--host", host,
                "--port", port,
                "--dbname", databaseName,
                "-f", backupFileName,
                "--verbose"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.environment().put("PGPASSWORD", password);

        try {
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("pg_dump completed successfully.");
                return true;
            } else {
                System.err.println("pg_dump failed with exit code: " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}