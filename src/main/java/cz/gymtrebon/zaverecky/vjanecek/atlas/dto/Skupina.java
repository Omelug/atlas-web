package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Skupina {

	private Integer id;
	
	private Integer idNadrizeneSkupiny;
	
	private String nazev, textSkupiny;

	List<Popisek> cesta = new ArrayList<>();
	
}
