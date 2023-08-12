package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@ToString
@Table(name = "udrlink", schema = "config")
public class UDRLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    User user;
    @ManyToOne
    Database database;
    @ManyToOne
    Role role;

    public UDRLink(User user, Database database, Role role) {
        this.user = user;
        this.database = database;
        this.role = role;
    }
}
