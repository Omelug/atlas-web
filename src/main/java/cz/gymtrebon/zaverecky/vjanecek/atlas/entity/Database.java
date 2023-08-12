package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "database", schema = "config")
public class Database {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "database")
    private List<UDRLink> UDRLinks;

    public Database(String name) {
       this.name = name;
       this.UDRLinks = null;
    }
    @Override
    public String toString(){
        return "Database:" + name;
    }
}