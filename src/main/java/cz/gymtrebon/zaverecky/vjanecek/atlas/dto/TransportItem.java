package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Color;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import lombok.Data;

import java.util.Set;

@Data
public class TransportItem {
	private Long id;
    private long IdParentGroup;
	private Typ typ;
	private String name;
	private String name2;
	private String author;
	private Set<Color> colors;
	private String text;
}

