package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.TransportRequestImage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "imageRequest")
@Getter @Setter
@NoArgsConstructor
public class RequestImage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator="native")
	@GenericGenerator(name="native", strategy = "native")
	private Integer id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;
    
	@Column(name="file")
	private String FileName;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	private Date modifyDate;

	public RequestImage(TransportRequestImage transportRequestImage, Optional<Request> request) {
		if (request.isEmpty()){
			return;
		}
		this.request = request.get();
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
