package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Zakazka {

	private Integer id;
	private Auto auto;
	private LocalDate datum;
	private String typ;
	private float cena;
	private String poznamka;
	
}
