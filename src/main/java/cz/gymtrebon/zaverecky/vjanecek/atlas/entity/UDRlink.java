package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "udrlink", schema = "config")
public class UDRlink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @ManyToOne
    User user;
    @ManyToOne
    Database database;
    @ManyToOne
    Role role;
}
