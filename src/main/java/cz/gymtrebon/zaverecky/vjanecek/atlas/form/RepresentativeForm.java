package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Photo;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class RepresentativeForm {
	
	private Integer id;
	
	@NotNull(message = "Parent group cant be null")
	private Integer idParentGroup;
	
	@NotEmpty(message = "Name cant be empty ")
	private String name;

	private String name2;
	private String author;
	private String text;
	private String color;
	List<Photo> images = new ArrayList<>();
	
	
}
