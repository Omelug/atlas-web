package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Item")
@Getter @Setter
public class Item {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator="native")
	@GenericGenerator(name="native", strategy = "native")
	@Column(name="id")
	private Integer id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_group_id")
    private Item parentGroup;
    
	@Column(name="typ", nullable=true)
	private Typ typ;
	
	@Column(nullable=true, length=250)
	private String name;
	
	@Column(nullable=true, length=250)
	private String name2;
	
	@Column( nullable=true, length=250)
	private String author;
	
	@Column( nullable=true, length=250)
	private String color;
	
	@Column( nullable=true, length=2500)
	private String text;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	private Date modifyDate;
	
	@ManyToMany(mappedBy="parentGroup", cascade = CascadeType.REMOVE)
	private List<Item> items = new ArrayList<>();
	
	@OneToMany(mappedBy="Item", cascade = CascadeType.REMOVE)
	private List<Image> images = new ArrayList<>();
	
	
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
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
