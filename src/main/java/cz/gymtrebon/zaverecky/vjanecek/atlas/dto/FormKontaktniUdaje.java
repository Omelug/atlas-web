package cz.gymtrebon.zaverecky.vjanecek.atlas.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class FormKontaktniUdaje {

	private Integer id;
	
	@Size(min=2, max=30)
	private String jmeno;
	
	@NotEmpty(message = "Uživatelské jméno nesmí být prázdné")
	@Size(min=2, max=30)
	private String prijmeni;
	
	@NotEmpty(message = "Telefon je povinná položka")
	@Size(min=9, max=13)
	private String telefon;
	
	@Email(message = "Koukej napsat spravnej email")
	private String email;
	
}
