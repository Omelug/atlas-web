package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Color;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class Representative {

	private Long id;
	private Long idParentGroup;
	private String name, name2, author, text;
	private Set<Color> colors;
	List<Photo> images = new ArrayList<>();

}
