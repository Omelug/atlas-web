package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BreadCrumb {

	private Integer id;
	private Typ typ;
	private String name;
	private String saveRepresentative;

}
