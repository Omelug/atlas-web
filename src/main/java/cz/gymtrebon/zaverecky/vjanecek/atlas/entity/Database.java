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
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @OneToMany(mappedBy = "database")
    private List<UDRlink> UDRlinks;


    public Database(String name) {
       this.name = name;
       this.UDRlinks = null;
    }

}