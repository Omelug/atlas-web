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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @OneToMany(mappedBy = "role")
    private List<UDRlink> UDRlinks;

    public Role(String name) {
        this.name = name;
        this.UDRlinks = null;
    }
}
