package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;
import lombok.Data;

@Data
public class TransportRequestImage {
	private Long id;
    private Integer itemId;
	private String name;
	private long createDate;
	private long modifyDate;
	private String requestMark;
}

