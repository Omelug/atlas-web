package cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CurrentDatabase{
    private static String currentDatabase;

    public static String getCurrentDatabase() {
        return currentDatabase;
    }
    public static void setCurrentDatabase(String database) {
        currentDatabase = database;
    }
}