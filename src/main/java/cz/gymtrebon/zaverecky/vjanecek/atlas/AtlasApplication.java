package cz.gymtrebon.zaverecky.vjanecek.atlas;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

//import cz.gymtrebon.zaverecky.vjanecek.atlas.kos.UserRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Polozka;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.opencsv.CSVReader;

import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ObrazekRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.PolozkaRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import lombok.extern.java.Log;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class,basePackages="cz.gymtrebon.zaverecky.vjanecek.atlas.repository")
@Log
public class AtlasApplication implements CommandLineRunner {

	@Autowired
	private PolozkaRepository polozkaRepo;
	
	@Autowired
	private ObrazekRepository obrazekRepo;

	@Autowired
	private AtlasService atlasService;

	public static void main(String[] args) {
		SpringApplication.run(AtlasApplication.class, args);
	}

	@Override
	public void run(String ... args) throws Exception {
		log.info("Atlas Command Line Runner");
		File directory = new File("../atlas-web/src/main/resources/pokusna_data/images");
		System.out.println(directory.getAbsolutePath());

/*
		Map<Integer, Integer> mapovaniimageId = new HashMap<>();
		Map<Integer, Integer> mapovaniId = new HashMap<>();

		Polozka p = new Polozka();
		p.setNadrizenaSkupina(null);
		p.setNazev("ATLAS");
		p.setTyp(Typ.ROOT);
		polozkaRepo.save(p);

		mapovaniId.put(0, p.getId());
*/
		/*File flowers = ResourceUtils.getFile("classpath:data/FLOWER.csv");
		List<String[]> flowerList = readAllLines(flowers);
		for (int i = 1; i < flowerList.size();i++) {
			String[] polozka = flowerList.get(i);
			int idPolozky = Integer.valueOf(polozka[0]);
			int idParenta = Integer.valueOf(polozka[1]);

			Polozka pol = new Polozka();
			Polozka parent = polozkaRepo.getById(mapovaniId.get(idParenta));
			pol.setNadrizenaSkupina(parent);
			if (Integer.valueOf(polozka[2]) == 1) {
				pol.setTyp(Typ.SKUPINA);
			}
			if (Integer.valueOf(polozka[2]) == 0) {
				pol.setTyp(Typ.ZASTUPCE);
			}
			pol.setNazev(polozka[3]);
			pol.setNazev2(polozka[4]);
			pol.setAutor(polozka[5]);
			pol.setText(polozka[6]);
			pol.setBarvy(polozka[7]);
			
			polozkaRepo.save(pol);
			mapovaniId.put(idPolozky, pol.getId());
		
		}


		File imagesCSV = ResourceUtils.getFile("classpath:data/IMAGE_FLOWER2.csv");
		List<String[]> imageList = readAllLines(imagesCSV);
		File imagesFolder = ResourceUtils.getFile("classpath:data/images/AppVitek2022");
		for (int i = 1; i < imageList.size();i++) {
			String[] polozka = imageList.get(i);
			int idParenta = Integer.valueOf(polozka[1]);
		
			String nameFromCsv = polozka[2];
			String[] parts = nameFromCsv.split("/");
			String fileName = parts[parts.length - 1].replaceAll("%20", " ");
			File imagesFile = new File(imagesFolder, fileName);	
			log.info("ImageFiesFile:   " + imagesFile.getAbsolutePath());
			atlasService.uploadObrazek(mapovaniId.get(idParenta), imagesFile);
			
		}
		*/
	}

	//@Bean
	//public PasswordEncoder passwordEncoder(){
	//	return NoOpPasswordEncoder.getInstance();
	//}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
	    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(); 
	    return multipartResolver;
	}		
	
	public List<String[]> readAllLines(File file) throws Exception {
	    try (Reader reader = new FileReader(file)) {
	        try (CSVReader csvReader = new CSVReader(reader)) {
	            return csvReader.readAll();
	        }
	    }
	}

}
