package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.TransportRequest;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.RequestStatus;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.RequestTyp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "request")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
    @GenericGenerator(name="native", strategy = "native")
    private Long id;
    private String requestMark;
    private String parentRequestMark;
    private Long link;
    private boolean local_visibility;
    private RequestTyp request_typ;
    private RequestStatus request_status;
    private String name;
    private String request_message;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdate")
    private Date createDate;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modifydate")
    private Date modifyDate;

    public Request(TransportRequest transportRequest) {
        requestMark = transportRequest.getRequestMark();
        parentRequestMark = transportRequest.getParentRequestMark();
        link = transportRequest.getLink();
        local_visibility = true;
        request_typ = getRequestTypFromInt(transportRequest.getRequest_typ());
        request_status = getRequestStatusFromInt(transportRequest.getRequest_status());
        name = transportRequest.getName();
        request_message = transportRequest.getRequest_message();
        createDate = new Date(transportRequest.getCreateDate());
        modifyDate = new Date(transportRequest.getModifyDate());
    }

    public static RequestTyp getRequestTypFromInt(int requestTypInt) {
        switch (requestTypInt) {
            case 0:
                return RequestTyp.ITEM;
            case 1:
                return RequestTyp.IMAGES;
            case 2:
                return RequestTyp.PERMISSIONS;
            case 3:
                return RequestTyp.DEBUG;
            case 4:
                return RequestTyp.SIMPLE_MESSAGE;
            default:
                return null;
        }
    }
    public static RequestStatus getRequestStatusFromInt(int requestStatusInt) {
        switch (requestStatusInt) {
            case 0:
                return RequestStatus.LOCAL;
            case 1:
                return RequestStatus.SENT;
            case 2:
                return RequestStatus.ADDED;
            case 3:
                return RequestStatus.EDITED;
            case 4:
                return RequestStatus.ACCEPTED;
            default:
                return null;
        }
    }

}
