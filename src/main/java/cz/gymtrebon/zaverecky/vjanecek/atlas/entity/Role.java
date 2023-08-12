package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "role", schema = "config")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "role")
    private List<UDRLink> UDRLinks;

    public Role(String name) {
        this.name = name;
        this.UDRLinks = null;
    }
}
