package cz.gymtrebon.zaverecky.vjanecek.atlas.dto.helpobjects;

import lombok.Data;

@Data
public class DatabaseListHelper {
    private String database;
    private String roles;

    public DatabaseListHelper(String database, String roles) {
        this.database = database;
        this.roles = roles;
    }
    public void addRole(String role){
        this.roles= roles+", "+ role;
    }
}
