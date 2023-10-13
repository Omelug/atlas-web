package cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb;

import cz.gymtrebon.zaverecky.vjanecek.atlas.service.SchemaService;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Data
public class CurrentDatabase{
    @Getter
    private static String currentDatabase;

    public static boolean setCurrentDatabase(String currentDatabase) {
        if(SchemaService.isValidSchemaName(currentDatabase)){
            CurrentDatabase.currentDatabase = currentDatabase;
            return true;
        }else{
            System.out.println("Database name " + currentDatabase + "is not valid");
            return false;
        }
    }
}