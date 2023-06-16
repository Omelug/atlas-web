package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Group {

	private Integer id;
	
	private Integer idParentGroup;
	
	private String name;
	private String text;

	List<Description> path = new ArrayList<>();
}
