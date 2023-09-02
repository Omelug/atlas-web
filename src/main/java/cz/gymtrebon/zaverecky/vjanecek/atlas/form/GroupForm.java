package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GroupForm {
	
	private Long id;
	
	//@NotNull(message = "Parent group cant be null") //TODO
	private Long idParentGroup;
	
	@NotEmpty(message = "Name cant be empty")
	private String name;
	
	private String text;
	
}
