package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import lombok.Data;

@Data
public class FindForm {
    private boolean open;
    private String name;
    private String name2;
    private Typ typ;
    private String parentGroup;
    private String author;
    private String color;
    private String text;
}