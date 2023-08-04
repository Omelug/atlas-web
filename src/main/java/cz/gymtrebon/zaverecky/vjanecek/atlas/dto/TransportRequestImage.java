package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;
import lombok.Data;

@Data
public class TransportRequestImage {
	private long id;
    private Integer itemId;
	private String name;
	private long createDate;
	private long modifyDate;
	private String requestMark;
}

