package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class SkupinaForm {
	
	private Integer id;
	
	private Integer idNadrizeneSkupiny;
	
	@NotEmpty(message = "Název je povinná položka")
	private String nazev;
	
}
