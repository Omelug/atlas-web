package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import lombok.Data;

@Data
public class Group {
	private Integer id;
	private Integer idParentGroup;
	private String name;
	private String text;
}
