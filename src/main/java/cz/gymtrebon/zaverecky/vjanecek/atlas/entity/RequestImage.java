package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.TransportRequestImage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "imagerequest")
@Getter @Setter
@NoArgsConstructor
public class RequestImage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;
    
	@Column(name="file")
	private String FileName;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdate")
	private Date createDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modifydate")
	private Date modifyDate;

	public RequestImage(TransportRequestImage transportRequestImage, Request request) {
		this.request = request;
		this.FileName = transportRequestImage.getName();
		this.createDate = new Date(transportRequestImage.getCreateDate());
		this.modifyDate = new Date(transportRequestImage.getModifyDate());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestImage other = (RequestImage) obj;
		if (id == null) {
			return other.id == null;
		} else return id.equals(other.id);
	}
}
