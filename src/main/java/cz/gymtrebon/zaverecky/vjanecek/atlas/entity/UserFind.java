package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "finds", schema="config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFind {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean open;

    private String parentGroup;
    private Typ typ;
    @Column( length=250)
    private String name;
    @Column(length=250)
    private String name2;
    @Column( length=250)
    private String author;
    @Column( length=250)
    private String color;
    @Column( length=2500)
    private String text;



    public UserFind(Optional<User> byName, String name, String name2, Typ typ, String parentGroup, String author, Set<Color> colors, String text, boolean open) {
        this.user = byName.orElseThrow();
        this.name = name;
        this.name2 = name2;
        this.typ = typ;
        this.parentGroup = parentGroup;
        this.author = author;
        this.color = serializeColors(colors);
        this.text = text;
        this.open = open;
    }

    public UserFind(Optional<User> byName) {
        this.user = byName.orElseThrow();
    }

    @Override
    public String toString() {
        return "UserFind{" +
                "id=" + id +
                ", user=" + user +
                ", open=" + open +
                ", parentGroup='" + parentGroup + '\'' +
                ", typ=" + typ +
                ", name='" + name + '\'' +
                ", name2='" + name2 + '\'' +
                ", author='" + author + '\'' +
                ", color='" + color + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
   public static String serializeColors(Set<Color> colors) {
        if (colors == null) {
            return "";
        }
        return colors.stream()
                .map(Color::getName)
                .collect(Collectors.joining(","));
    }

    public static Set<Color> deserializeColors(String serializedColors) {
        Set<Color> colors = new HashSet<>();
        if (serializedColors == null) {
            return colors;
        }
        String[] colorNames = serializedColors.split(",");

        for (String colorName : colorNames) {
            colors.add(new Color(colorName.trim()));
        }

        return colors;
    }

    public void setColors(Set<Color> colors) {
        setColor(serializeColors(colors));
    }
    public Set<Color>  getColors() {
        return deserializeColors(color);
    }
}
