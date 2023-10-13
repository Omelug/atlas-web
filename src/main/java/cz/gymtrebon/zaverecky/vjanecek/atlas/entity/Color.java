package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "color")
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "colors")
    private Set<Item> itemList;

    public Color(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
