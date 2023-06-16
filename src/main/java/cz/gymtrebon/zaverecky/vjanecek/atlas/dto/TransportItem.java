package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import lombok.Data;

@Data
public class TransportItem {
	private Integer id;
    private long IdParentGroup;
	private Typ typ;
	private String name;
	private String name2;
	private String author;
	private String color;
	private String text;
}

