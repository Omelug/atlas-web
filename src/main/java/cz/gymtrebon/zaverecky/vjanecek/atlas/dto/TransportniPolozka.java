package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import lombok.Data;

@Data
public class TransportniPolozka {
	private Integer id;
    private long nadrizenaSkupinaid;
	private Typ typ;
	private String nazev;
	private String nazev2;
	private String autor;
	private String barvy;
	private String text;
}

