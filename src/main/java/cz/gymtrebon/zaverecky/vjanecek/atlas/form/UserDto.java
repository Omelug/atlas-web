package cz.gymtrebon.zaverecky.vjanecek.atlas.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class UserDto {

 	@NotBlank(message = "Řekněte jméno!")
	private String name;
	@NotBlank(message = "Password!")
	private String password;

	private Date firstLogin;
	private Date lastLogin;
	
}
