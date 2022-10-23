package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Skupina {

	private Integer id;
	
	private String nazev;

	List<Breadcrumb> cesta = new ArrayList<>();
	
}
