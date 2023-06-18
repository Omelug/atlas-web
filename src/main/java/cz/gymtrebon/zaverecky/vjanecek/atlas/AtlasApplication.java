package cz.gymtrebon.zaverecky.vjanecek.atlas;

import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.service.AtlasService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.File;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class,basePackages="cz.gymtrebon.zaverecky.vjanecek.atlas.repository")
@EntityScan({"cz.gymtrebon.zaverecky.vjanecek.atlas.entity"})
@Log
public class AtlasApplication implements CommandLineRunner {

	@Autowired
	private ItemRepository ItemRepo;
	
	@Autowired
	private ImageRepository ImageRepo;
	@Autowired
	private UDRlinkRepository udrLinkRepository;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private DatabaseRepository dataRepo;
	@Autowired
	private RoleRepository roleRepo;
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

		//TODO spusteni poprve, potom to nějak oddelit
		/*
		Database publicdb = dataRepo.save(new Database("public"));
		User admin = userRepo.save(new User("admin", "admin", publicdb.getName()));
		Role adminrole = roleRepo.save(new Role("ADMIN"));
		udrLinkRepository.save(new UDRlink(admin, publicdb, adminrole));

		roleRepo.save(new Role("EDITOR"));
		roleRepo.save(new Role("USER"));
		*/

		/*Map<Integer, Integer> mapovaniimageId = new HashMap<>();
		Map<Integer, Integer> mapovaniId = new HashMap<>();

		Item p = new Item();
		p.setParentGroup(null);
		p.setName("ATLAS");
		p.setTyp(Typ.ROOT);
		ItemRepo.save(p);

		mapovaniId.put(0, p.getId());

		File flowers = ResourceUtils.getFile("classpath:data/FLOWER.csv");
		List<String[]> flowerList = readAllLines(flowers);
		for (int i = 1; i < flowerList.size();i++) {
			String[] Item = flowerList.get(i);
			int idPolozky = Integer.valueOf(Item[0]);
			int idParenta = Integer.valueOf(Item[1]);

			Item pol = new Item();
			Item parent = ItemRepo.getById(mapovaniId.get(idParenta));
			pol.setParentGroup(parent);
			if (Integer.valueOf(Item[2]) == 1) {
				pol.setTyp(Typ.GROUP);
			}
			if (Integer.valueOf(Item[2]) == 0) {
				pol.setTyp(Typ.REPRESENTATIVE);
			}
			pol.setName(Item[3]);
			pol.setName2(Item[4]);
			pol.setAuthor(Item[5]);
			pol.setText(Item[6]);
			pol.setColor(Item[7]);
			
			ItemRepo.save(pol);
			mapovaniId.put(idPolozky, pol.getId());
		
		}


		File imagesCSV = ResourceUtils.getFile("classpath:data/IMAGE_FLOWER2.csv");
		List<String[]> imageList = readAllLines(imagesCSV);
		File imagesFolder = ResourceUtils.getFile("classpath:data/images/AppVitek2022");
		for (int i = 1; i < imageList.size();i++) {
			String[] Item = imageList.get(i);
			int idParenta = Integer.valueOf(Item[1]);
		
			String nameFromCsv = Item[2];
			String[] parts = nameFromCsv.split("/");
			String fileName = parts[parts.length - 1].replaceAll("%20", " ");
			File imagesFile = new File(imagesFolder, fileName);	
			log.info("ImageFiesFile:   " + imagesFile.getAbsolutePath());
			atlasService.uploadImage(mapovaniId.get(idParenta), imagesFile);
		}
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
	    }*/
	}

}
