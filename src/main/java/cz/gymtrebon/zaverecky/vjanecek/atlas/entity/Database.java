package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "database", schema = "config")
public class Database {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @OneToMany(mappedBy = "database")
    private List<UDRlink> UDRlinks;


}