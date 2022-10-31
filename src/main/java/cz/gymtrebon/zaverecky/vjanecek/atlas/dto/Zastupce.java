package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Zastupce {

	private Integer id;
	
	private Integer idNadrizeneSkupiny;
	
	private String nazev;
	
	List<Popisek> cesta = new ArrayList<>();
}
