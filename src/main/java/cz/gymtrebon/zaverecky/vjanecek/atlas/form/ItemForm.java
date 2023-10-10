package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Group;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Photo;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Item;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ItemForm {
	private Long id;

	@NotNull(message = "Parent group cant be null") //TODO povolit null u search
	private Long idParentGroup;
	
	@NotEmpty(message = "Name cant be empty")
	private String name;

	private String name2;
	private String author;
	private String text;
	List<Photo> images = new ArrayList<>();

	public ItemForm(Item item) {
		this.id = item.getId();
		this.name = item.getName();
		this.text = item.getText();
	}

	public ItemForm(Group group) {
		this.id = group.getId();
		this.idParentGroup = group.getIdParentGroup();
		this.name = group.getName();
		this.text = group.getText();
	}
}
