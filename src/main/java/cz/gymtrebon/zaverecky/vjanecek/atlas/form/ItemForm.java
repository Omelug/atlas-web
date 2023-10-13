package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Photo;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Item;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ItemForm {
	private Long id;

	//@NotNull(message = "Parent group cant be null") //TODO povolit null u search
	private Long idParentGroup;
	@NotBlank(message = "Name cant be blank")
	@NotNull(message = "Name cant be null")
	private String name;
	private String formType;
	private String name2;
	private String author;
	private String text;
	List<Photo> images = new ArrayList<>();

	public ItemForm(Item item) {
		this.id = item.getId();
		if (item.getParentGroup() == null) {
			this.idParentGroup = null;
		}else{
			this.idParentGroup = item.getParentGroup().getId();
		}
		this.name = item.getName();
		this.name2 = item.getName2();
		this.author = item.getAuthor();
		this.text = item.getText();
		this.formType = item.getTyp().name();
	}

	public static Typ getType(String StringType) {
		Typ typ = null;
		if (StringType.equals(Typ.GROUP.name())) {
			typ = Typ.GROUP;
		}
		if (StringType.equals(Typ.REPRESENTATIVE.name())) {
			typ = Typ.REPRESENTATIVE;
		}
		if (StringType.equals(Typ.ROOT.name())) {
			typ = Typ.ROOT;
		}
		return typ;
	}
}
