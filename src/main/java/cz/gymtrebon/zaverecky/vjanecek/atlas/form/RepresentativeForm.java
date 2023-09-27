package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Photo;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Color;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class RepresentativeForm {
	
	private Long id;
	
	@NotNull(message = "Parent group cant be null")
	private Long idParentGroup;
	
	@NotEmpty(message = "Name cant be empty ")
	private String name;

	private String name2;
	private String author;
	private String text;
	private Set<Color> colors;
	List<Photo> images = new ArrayList<>();

    public void setColors(Set<Color> colors) {
    }
}
