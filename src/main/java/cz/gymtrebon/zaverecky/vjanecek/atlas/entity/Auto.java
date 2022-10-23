package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Auto {

	private Integer id;
	private Zakaznik zakaznik;
	private String registracniZnacka;
	private String znacka;
	private String model;
	private int rokVyroby;
	private int km;
	private LocalDate pristiProhlidka;
	private List<Zakazka> zakazky = new ArrayList<>();
	
}
