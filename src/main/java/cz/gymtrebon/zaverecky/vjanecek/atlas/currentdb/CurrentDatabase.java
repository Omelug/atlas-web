package cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CurrentDatabase{
    public static final String DEFAULT_DATABASE = "public";
    private static String currentDatabase;

    public static String getCurrentDatabase() {
        if (currentDatabase == null){
            return DEFAULT_DATABASE;
        }
        return currentDatabase;
    }
    public static void setCurrentDatabase(String database) {
        currentDatabase = database;
    }
}