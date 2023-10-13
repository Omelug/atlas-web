package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.UserFind;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.ColorService;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FindForm {
    private boolean open;
    private String name;
    private String name2;
    private Typ typ;
    private String parentGroup;
    private String author;
    private List<String> colors;
    private String text;

  public FindForm(UserFind userFind) {
    this.open = userFind.isOpen();
    this.name = userFind.getName();
    this.name2 = userFind.getName2();
    this.typ = userFind.getTyp();
    this.parentGroup = userFind.getParentGroup();
    this.author = userFind.getAuthor();
    this.colors = ColorService.colorsToString(userFind.getColors());
    //TODO this.colors = userFind.getColor();
    this.text = userFind.getText();
  }
}