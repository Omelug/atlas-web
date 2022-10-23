package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Zakaznik {

	private Integer id;
	private String jmeno;
	private String prijmeni;
	private String telefon;
	private String email;
	private List<Auto> auta = new ArrayList<>();
	
}
