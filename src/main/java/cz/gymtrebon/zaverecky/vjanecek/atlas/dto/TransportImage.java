package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;
import lombok.Data;

@Data
public class TransportImage {
	private Long id;
    private Long itemId;
	private String name;
	private String createDate;
	private String modifyDate;
}

