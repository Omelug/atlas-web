package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.Fotka;
import lombok.Data;

@Data
public class ZastupceForm {
	
	private Integer id;
	
	@NotNull(message = "Nadřízená skupina je povinná položka")
	private Integer idNadrizeneSkupiny;
	
	@NotEmpty(message = "Název je povinná položka")
	private String nazev;

	private String nazev2;
	
	private String autor;
	
	private String text;
	
	private String barvy;
	
	List<Fotka> fotky = new ArrayList<>();
	
	
}
