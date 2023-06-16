package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Image;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Item;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ImageRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtlasService {
	private final ItemRepository ItemRepo;
	private final ImageRepository ImageRepo;
	
	@Value("${images.path}")
	private String imagesFolder;
	private static String cestaKObrazkum;

	public Group najdiRootSkupinu() {
		List<Item> polozky = ItemRepo.findAllByTyp(Typ.ROOT);
		for (Item Item : polozky) {
			log.info("Item: " + Item.getName());
		}

		return ItemToSkupina(ItemRepo.findByTyp(Typ.ROOT));
	}

	public List<Description> seznamSkupin() {
		List<Description> skupiny = new ArrayList<>();

		skupiny.add(ItemToDescription(ItemRepo.findByTyp(Typ.ROOT)));
		for (Item Item : ItemRepo.findAllByTyp(Typ.GROUP)) {
			Description p = new Description();
			p.setId(Item.getId());
			p.setName(Item.getName());
			skupiny.add(p);
		}
		return skupiny;
	}

	public List<Group> seznamPodskupin(Integer idNadrizene) {
		Item nadrizena = ItemRepo.getById(idNadrizene);
		List<Group> skupiny = new ArrayList<>();
		for (Item Item : ItemRepo.findByParentGroupAndTyp(nadrizena, Typ.GROUP)) {
			skupiny.add(ItemToSkupina(Item));
		}
		return skupiny;
	}

	public List<Representative> seznamZastupcu(Integer idNadrizene) {
		Item nadrizena = ItemRepo.getById(idNadrizene);
		List<Representative> representative = new ArrayList<>();
		for (Item Item : ItemRepo.findByParentGroupAndTyp(nadrizena, Typ.REPRESENTATIVE)) {
			representative.add(ItemToRepresentative(Item));
		}
		return representative;
	}

	public Integer vytvoritSkupinu(Integer idParentGroup, String nazev, String text) {

		Item nadrizena = ItemRepo.getById(idParentGroup);

		Item p = new Item();
		p.setName(nazev);
		p.setText(text);
		p.setTyp(Typ.GROUP);
		p.setParentGroup(nadrizena);
		ItemRepo.save(p);
		return p.getId();
	}

	public Integer ulozSkupinu(Integer idParentGroup, Integer idSkupiny, String nazev, String text) {

		Item nadrizena = ItemRepo.getById(idParentGroup);
		Item skupina = ItemRepo.getById(idSkupiny);
		skupina.setName(nazev);
		skupina.setText(text);
		skupina.setTyp(Typ.GROUP);
		skupina.setParentGroup(nadrizena);
		ItemRepo.save(skupina);
		return skupina.getId();
	}

	public void smazatSkupinu(Integer skupinaId) {
		List<Group> podskupiny = seznamPodskupin(skupinaId);
		for (Group group : podskupiny) {
			smazatSkupinu(group.getId());
		}
		List<Representative> zastupci = seznamZastupcu(skupinaId);
		for (Representative representative : zastupci) {
			smazatPolozku(representative.getId());
		}
		smazatPolozku(skupinaId);
	}

	public void smazatPolozku(Integer representativeId) {
		if (!(ItemRepo.getById(representativeId).getTyp() == Typ.ROOT)) {
			ItemRepo.deleteById(representativeId);
		}
	}

	public Integer vytvoritRepresentative(Integer idParentGroup, String name, String name2, String author, String color,
			String text) {

		Item nadrizena = ItemRepo.getById(idParentGroup);

		Item representative = new Item();
		representative.setName(name);
		representative.setName2(name2);
		representative.setAuthor(author);
		representative.setColor(color);
		representative.setText(text);

		representative.setTyp(Typ.REPRESENTATIVE);
		representative.setParentGroup(nadrizena);
		ItemRepo.save(representative);
		return representative.getId();
	}

	public Integer ulozRepresentative(Integer idParentGroup, Integer groupId, String name, String name2,
			String author, String color, String text) {

		Item nadrizena = ItemRepo.getById(idParentGroup);
		Item representative = ItemRepo.getById(groupId);
		representative.setName(name);
		representative.setName2(name2);
		representative.setAuthor(author);
		representative.setColor(color);
		representative.setText(text);

		representative.setTyp(Typ.REPRESENTATIVE);
		representative.setParentGroup(nadrizena);
		ItemRepo.save(representative);
		return representative.getId();
	}

	public Group najdiSkupinuDleId(Integer idSkupiny) {
		Item Item = ItemRepo.getById(idSkupiny);
		return ItemToSkupina(Item);
	}

	public Representative najdiRepresentativeDleId(Integer idRepresentative) {
		Item Item = ItemRepo.getById(idRepresentative);
		return ItemToRepresentative(Item);
	}

	public Image najdiImageDleId(Integer id) {
		return ImageRepo.getById(id);
	}

	public Group ItemToSkupina(Item Item) {
		Group s = new Group();
		s.setId(Item.getId());
		s.setName(Item.getName());
		s.setText(Item.getText());
		if (Item.getParentGroup() != null) {
			s.setIdParentGroup(Item.getParentGroup().getId());
		}

		Item pom = Item.getParentGroup();
		while (pom != null && pom.getTyp() != Typ.ROOT) {
			Description b = new Description();
			b.setId(pom.getId());
			b.setName(pom.getName());
			s.getPath().add(0, b);
			pom = pom.getParentGroup();
		}

		return s;
	}

	public Representative ItemToRepresentative(Item Item) {
		Representative representative = new Representative();
		representative.setId(Item.getId());
		representative.setName(Item.getName());
		representative.setName2(Item.getName2());
		representative.setAuthor(Item.getAuthor());
		representative.setColor(Item.getColor());
		representative.setText(Item.getText());
		if (Item.getParentGroup() != null) {
			representative.setIdParentGroup(Item.getParentGroup().getId());
		}
		if (Item.getImages() != null) {
			for (Image Image : Item.getImages()) {
				representative.getImages().add(PhotoFromImage(Image));
			}
		}
		Item pom = Item.getParentGroup();
		while (pom != null && pom.getTyp() != Typ.ROOT) {
			Description b = new Description();
			b.setId(pom.getId());
			b.setName(pom.getName());
			representative.getPath().add(0, b);
			pom = pom.getParentGroup();
		}

		return representative;
	}

	public Photo PhotoFromImage(Image Image) {
		Photo f = new Photo();
		f.setId(Image.getId());
		f.setName(Image.getFileName());
		f.setUrl("/image/" + Image.getId());
		return f;
	}

	public Description ItemToDescription(Item Item) {
		Description p = new Description();
		p.setId(Item.getId());
		p.setName(Item.getName());
		return p;
	}

	public void uploadImage(Integer ItemId, MultipartFile file) {

		Image o = new Image();
		o.setFileName(file.getOriginalFilename());
		Item p = ItemRepo.getById(ItemId);
		o.setItem(p);
		ImageRepo.save(o);
		log.info("Uploading file " + file.getOriginalFilename());
		try {
			cestaKObrazkum = new File(imagesFolder).getAbsolutePath();
			File f = new File(cestaKObrazkum, String.valueOf(o.getId()));
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			log.error("Error while saving image", e);
			throw new RuntimeException("Error while saving image", e);
		}

	}

	public void uploadImage(Integer ItemId, File file) {

		Item p = ItemRepo.getById(ItemId);

		Image o = new Image();
		o.setItem(p);
		o.setFileName(file.getName());

		ImageRepo.save(o);
		log.info("Importing file " + file.getAbsolutePath());
		try {
			cestaKObrazkum = new File(imagesFolder).getAbsolutePath();
			File f = new File(cestaKObrazkum, String.valueOf(o.getId()));
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
			byte[] buffer = new byte[1024];
			int lengthRead;
			while ((lengthRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, lengthRead);
				out.flush();
			}
			out.close();
			in.close();
		} catch (IOException e) {
			log.error("Error while saving image", e);
			throw new RuntimeException("Error while saving image", e);
		}
	}

	public File souborObrazku(Integer ImageId) {
		Image o = ImageRepo.getById(ImageId);
		cestaKObrazkum = new File(imagesFolder).getAbsolutePath();
		return new File(cestaKObrazkum, String.valueOf(o.getId()));
	}

	public void deleteImage(Integer id, Integer ImageId) {
		Image o = ImageRepo.getById(ImageId);
		cestaKObrazkum = new File(imagesFolder).getAbsolutePath();
		File f = new File(cestaKObrazkum, String.valueOf(o.getId()));
		f.delete();

		ImageRepo.delete(o);

	}

	// rekurzivni funkce pro zanoreni do podpolozek
	public TransportItem ItemToTP(Integer idPolozky) {
		TransportItem tp = new TransportItem();
		Item Item = ItemRepo.getById(idPolozky);

		tp.setName(Item.getName());
		tp.setText(Item.getText());
		tp.setTyp(Item.getTyp());
		try {
			tp.setIdParentGroup(Item.getParentGroup().getId());
		} catch (Exception e) {
			tp.setIdParentGroup(0); //TODO  Tady je id pro hlavni slozku
			log.info("getParentGroup je NULL");
		}

		tp.setId(Item.getId());
		tp.setAuthor(Item.getAuthor());
		tp.setColor(Item.getColor());
		tp.setName2(Item.getName2());

		return tp;
	}

	public void rekurzivniPridavaniPolozek(Integer idPolozky, List<TransportItem> database) {
		pridatDoTransportItem(idPolozky, database);
		for (Item podItem : ItemRepo.getById(idPolozky).getItems()) {
			rekurzivniPridavaniPolozek(podItem.getId(), database);
		}
	}

	public void pridatDoTransportItem(Integer idPolozky, List<TransportItem> listTP) {
		TransportItem tp = ItemToTP(idPolozky);
		listTP.add(tp);
	}

	public void pridavaniObrazku(List<TransportImage> obrazky) {
		List<Image> obrazkyList = ImageRepo.findAll();
		for (Image Image : obrazkyList) {
			TransportImage to = ImageToTO(Image);
			obrazky.add(to);
		}
	}

	private TransportImage ImageToTO(Image Image) {
		TransportImage to = new TransportImage();
		to.setId(Image.getId());
		to.setFileName(Image.getFileName());
		to.setItemid(Image.getItem().getId());
		return to;
	}

	public InputStream inputStream(Integer ImageId) throws FileNotFoundException {
		Image o = ImageRepo.getById(ImageId);
		cestaKObrazkum = new File(imagesFolder).getAbsolutePath();
		File f = new File(cestaKObrazkum, String.valueOf(o.getId()));
		InputStream vysledek = new FileInputStream(f);
		return vysledek;
	}

}
