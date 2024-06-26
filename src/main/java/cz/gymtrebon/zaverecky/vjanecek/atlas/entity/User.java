package cz.gymtrebon.zaverecky.vjanecek.atlas.entity;

import cz.gymtrebon.zaverecky.vjanecek.atlas.form.RegistrationForm;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user",schema = "config")
public class User {
    public static final String EDITOR = "EDITOR";
	public static final String ADMIN = "ADMIN";
	public static final String USER = "USER";

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(length=250)
	private String name;
	@Column(length=250)
	private String password;

    private boolean active;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date firstLogin;

	private Date lastLogin;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modifydate")
	private Date modifyDate;

	private String currentDB_name;

	@OneToMany(mappedBy = "user")
	private List<UDRLink> udrLinks;

	public User(String username, String password, String databaseName) {
		this.name = username;
        this.password = password;
        this.active = true;
		Date actualDate = new Date();
       	firstLogin = actualDate;
        lastLogin = actualDate;
        modifyDate = actualDate;
        currentDB_name = databaseName;
	}
	public User(String username, String password, String databaseName, boolean hash) {
		this(username, password, databaseName);
		if (hash){
			setPassword(password);
		}
	}
	public User(RegistrationForm registrationForm) {
			this.name = registrationForm.getUsername();
			this.setPassword(registrationForm.getPassword());
			this.active = true;
			Date actualDate = new Date();
			firstLogin = actualDate;
			lastLogin = actualDate;
			modifyDate = actualDate;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof User)){
			return false;
		}
		if (this == o) return true;
		if (Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		User user = (User) o;
		return id != null && Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	public void setPassword(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		this.password = passwordEncoder.encode(password);
	}

	@Override
	public String toString() {
		return "User{" + "id=" + id + ", name='" + name+"}";
	}
}