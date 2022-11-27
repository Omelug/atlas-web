package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import java.util.ArrayList;
import java.util.List;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import lombok.Data;

@Data
public class TransportniPolozka {
	
	private Typ typ;

	private String nazev;
	
	private String nazev2;
	
	private String autor;
	
	List<TransportniPolozka> podpolozky = new ArrayList<>();
	
}

