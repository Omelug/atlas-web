package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "polozka")
@Getter @Setter
public class Polozka {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator="native")
	@GenericGenerator(name="native", strategy = "native")
	@Column(name="id")
	private Integer id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nadrizena_skupina_id")
    private Polozka nadrizenaSkupina;
    
	@Column(name="typ", nullable=true)
	private Typ typ;
	
	@Column(name="nazev", nullable=true, length=250)
	private String nazev;
	
	@Column(name="nazev2", nullable=true, length=250)
	private String nazev2;
	
	@Column(name="autor", nullable=true, length=250)
	private String autor;
	
	@Column(name="barvy", nullable=true, length=250)
	private String barvy;
	
	@Column(name="text", nullable=true, length=2500)
	private String text;
	
	@ManyToMany(mappedBy="nadrizenaSkupina", cascade = CascadeType.REMOVE)
	private List<Polozka> polozky = new ArrayList<>();
	
	@OneToMany(mappedBy="polozka", cascade = CascadeType.REMOVE)
	private List<Obrazek> obrazky = new ArrayList<>();
	
	
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
		Polozka other = (Polozka) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
