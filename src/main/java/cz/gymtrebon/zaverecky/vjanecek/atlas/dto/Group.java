package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import lombok.Data;

@Data
public class Group {
	private boolean isRoot;
	private Long id;
	private Long idParentGroup;
	private String name;
	private String text;
}
