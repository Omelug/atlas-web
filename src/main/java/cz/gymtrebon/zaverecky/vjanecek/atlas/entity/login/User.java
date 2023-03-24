package cz.gymtrebon.zaverecky.vjanecek.atlas.entity.login;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "users",uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="user_id")
	private Long user_id;
	@Column(name="name", nullable=true, length=250)
	private String username;
	@Column(name="password", nullable=true, length=250)
	private String password;
    @Column(name="active", nullable=true)
    private boolean active;

	private String roles;

	@Column(name="first_login", nullable=true)
	private Date firstLogin;
	@Column(name="last_login", nullable=true)
	private Date lastLogin;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	private Date modifyDate;


	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		User user = (User) o;
		return user_id != null && Objects.equals(user_id, user.user_id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	public void setUserName(String userName) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
