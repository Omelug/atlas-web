package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import lombok.Data;

@Data
public class TransportRequest {
    private String database;
    private String requestMark;
    private String parentRequestMark;
    private Long link;
    private Integer request_typ;
    private Integer request_status;
    private String name;
    private String request_message;
    private Long createDate;
    private Long modifyDate;
}
