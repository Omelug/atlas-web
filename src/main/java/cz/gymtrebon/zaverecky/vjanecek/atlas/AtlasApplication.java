package cz.gymtrebon.zaverecky.vjanecek.atlas;

import com.opencsv.CSVReader;
import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.DatabaseAccess;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RequiredArgsConstructor
@EnableJpaRepositories(basePackageClasses = UserRepository.class,basePackages="cz.gymtrebon.zaverecky.vjanecek.atlas.repository")
@EntityScan({"cz.gymtrebon.zaverecky.vjanecek.atlas.entity"})
@Log
public class AtlasApplication implements CommandLineRunner {

	private final ItemRepository itemRepository;
	private final UDRLinkRepository udrLinkRepository;
	private final UserRepository userRepo;
	private final DatabaseRepository dataRepo;
	private final RoleRepository roleRepo;
	private final AtlasService atlasService;
	private final SchemaService schemaService;
	static String first_database_name = "public";


	public static void main(String[] args) {SpringApplication.run(AtlasApplication.class, args);
	}

	@Override
	public void run(String ... args) throws Exception {
		log.info("Atlas Command Line Runner");
		boolean start1 = false;
		boolean start2 = false;
		boolean testData = false;
		for (String arg : args) {
			switch (arg){
				case "start1":
					start1 = true;
				case "start2":
					start2 = true;
				case "testData":
					testData = true;
			}
		}


		File directory = new File("../atlas-web/src/main/resources/pokusna_data/images");
		System.out.println(directory.getAbsolutePath());

		if (start1) {
			CurrentDatabase.setCurrentDatabase(first_database_name);
			schemaService.createTablesInSchema(first_database_name);
		}

		if (start2){
			CurrentDatabase.setCurrentDatabase(first_database_name);
			Database firstDB = new Database(first_database_name);
			firstDB.setDatabaseAccess(DatabaseAccess.PUBLIC);
			dataRepo.save(firstDB);
			User admin = userRepo.save(new User("admin", "$2a$10$X3iBZZQgGfvxx4olu6YwCebHTBiV9iqcEAN3Anb4VbljJZV3oGhPa", first_database_name)); //TODO mozna staci admin

			Role adminrole = roleRepo.save(new Role("ADMIN"));
			roleRepo.save(new Role("EDITOR"));
			roleRepo.save(new Role("USER"));

			udrLinkRepository.save(new UDRLink(admin, null, adminrole)); //TODD null neni vyzkousene

		}

		if (testData) {
			CurrentDatabase.setCurrentDatabase(first_database_name);
			Map<Long, Long> mapovaniId = new HashMap<>();

			mapovaniId.put(0L, itemRepository.findByTyp(Typ.ROOT).orElseThrow().getId());

			File flowers = ResourceUtils.getFile("classpath:data/FLOWER.csv");
			List<String[]> flowerList = readAllLines(flowers);
			for (int i = 1; i < flowerList.size(); i++) {
				String[] Item = flowerList.get(i);
				long itemId = Integer.parseInt(Item[0]);
				long parentId = Integer.parseInt(Item[1]);

				Item pol = new Item();
				Item parent = itemRepository.getById(mapovaniId.get(parentId));
				pol.setParentGroup(parent);
				if (Integer.parseInt(Item[2]) == 1) {
					pol.setTyp(Typ.GROUP);
				}
				if (Integer.parseInt(Item[2]) == 0) {
					pol.setTyp(Typ.REPRESENTATIVE);
				}
				pol.setName(Item[3]);
				pol.setName2(Item[4]);
				pol.setAuthor(Item[5]);
				pol.setText(Item[6]);
				//pol.setColors(Item[7]); //TODO

				itemRepository.save(pol);
				mapovaniId.put(itemId, pol.getId());

			}


			File imagesCSV = ResourceUtils.getFile("classpath:data/IMAGE_FLOWER2.csv");
			List<String[]> imageList = readAllLines(imagesCSV);
			File imagesFolder = ResourceUtils.getFile("classpath:data/images/AppVitek2022");
			for (int i = 1; i < imageList.size();i++) {
				String[] Item = imageList.get(i);
				long parentId = Long.parseLong(Item[1]);

				String nameFromCsv = Item[2];
				String[] parts = nameFromCsv.split("/");
				String fileName = parts[parts.length - 1].replaceAll("%20", " ");
				File imageFile = new File(imagesFolder, fileName);
				log.info("ImageFiesFile:   " + imageFile.getAbsolutePath());
				atlasService.uploadImage(mapovaniId.get(parentId), imageFile);
			}
		}
	}


	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
	    return new CommonsMultipartResolver();
	}		
	
	public List<String[]> readAllLines(File file) throws Exception {
	    try (Reader reader = new FileReader(file)) {
	        try (CSVReader csvReader = new CSVReader(reader)) {
	            return csvReader.readAll();
	        }
	    }
	}

}
