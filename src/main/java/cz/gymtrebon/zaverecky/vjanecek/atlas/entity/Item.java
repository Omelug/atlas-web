package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import lombok.*;
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
@NoArgsConstructor
@AllArgsConstructor
public class Item {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GenericGenerator(name="native", strategy = "native")
	@Column(name="id")
	private Integer id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_group_id")
    private Item parentGroup;
    
	@Column(name="typ", nullable=false)
	private Typ typ;
	
	@Column(nullable = false, length=250)
	private String name;
	
	@Column(length=250)
	private String name2;
	
	@Column( length=250)
	private String author;
	
	@Column( length=250)
	private String color;
	
	@Column( length=2500)
	private String text;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	private Date modifyDate;
	
	@OneToMany(mappedBy="parentGroup", cascade = CascadeType.REMOVE)
	private List<Item> items = new ArrayList<>();
	
	@OneToMany(mappedBy="Item", cascade = CascadeType.REMOVE)
	private List<Image> images = new ArrayList<>();

	public Item(String name, String name2, String author, String color, String text, Typ typ, Item nadrizena) {
		setName(name);
		setName2(name2);
		setAuthor(author);
		setColor(color);
		setText(text);
		setTyp(typ);
		setParentGroup(nadrizena);
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
		Item other = (Item) obj;
		if (id == null) {
			return other.id == null;
		} else return id.equals(other.id);
	}
}
