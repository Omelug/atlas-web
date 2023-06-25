package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Representative {

	private Integer id;
	
	private Integer idParentGroup;
	
	private String name, name2, author, color, text;

	List<Photo> images = new ArrayList<>();
}
