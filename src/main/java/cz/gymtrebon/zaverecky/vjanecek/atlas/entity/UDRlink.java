package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "udrlink", schema = "config")
public class UDRlink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    User user;
    @ManyToOne
    Database database;
    @ManyToOne
    Role role;

    public UDRlink(User admin, Database publicdb, Role adminrole) {
        this.user = admin;
        this.database = publicdb;
        this.role = adminrole;
    }
}
