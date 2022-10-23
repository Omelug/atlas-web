package cz.gymtrebon.zaverecky.vjanecek.atlas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Polozka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.PolozkaRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import lombok.extern.java.Log;

@SpringBootApplication
@EnableJpaRepositories(basePackages="cz.gymtrebon.zaverecky.vjanecek.atlas.repository") 
@Log
public class AtlasApplication implements CommandLineRunner {

	@Autowired
	private PolozkaRepository polozkaRepo;
	
	@Autowired
	private AtlasService atlasService;
	
	public static void main(String[] args) {
		SpringApplication.run(AtlasApplication.class, args);
	}

	@Override
	public void run(String ... args) throws Exception {
		log.info("Atlas Command Line Runner");
		
		Polozka p = new Polozka();
		p.setNadrizenaSkupina(null);
		p.setNazev("ATLAS");
		p.setTyp(Typ.ROOT);
		polozkaRepo.save(p);

	}	
	
	
}
