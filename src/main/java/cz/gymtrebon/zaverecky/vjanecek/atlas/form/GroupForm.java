package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class GroupForm {
	
	private Integer id;
	
	@NotNull(message = "Parent group cant be null")
	private Integer idParentGroup;
	
	@NotEmpty(message = "Name cant be empty")
	private String name;
	
	private String text;
	
}
