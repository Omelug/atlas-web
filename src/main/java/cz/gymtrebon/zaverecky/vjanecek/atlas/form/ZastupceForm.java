package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ZastupceForm {
	
	private Integer id;
	
	@NotNull(message = "Nadřízená skupina je povinná položka")
	private Integer idNadrizeneSkupiny;
	
	@NotEmpty(message = "Název je povinná položka")
	private String nazev;

	private String nazev2;
	
	@NotEmpty(message = "Autor je povinná položka")
	private String autor;
	
	private String text;
	
	private String barvy;
	
	
}
