package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtlasService {
	private final ItemRepository itemRepository;
	private final ImageRepository imageRepository;
	
	@Value("${images.path}")
	private String imagesFolder;
	private static String cestaKObrazkum;

	public Group najdiRootSkupinu() {
		List<Item> polozky = itemRepository.findAllByTyp(Typ.ROOT);
		for (Item Item : polozky) {
			log.info("Item: " + Item.getName());
		}

		return ItemToSkupina(itemRepository.findByTyp(Typ.ROOT).get());
	}
	public Group findORcreateGroup() {
		Optional<Item> polozka = itemRepository.findByTyp(Typ.ROOT);
		if(polozka.isEmpty()) {
			Item p = new Item();
			p.setParentGroup(null);
			p.setName(CurrentDatabase.getCurrentDatabase());
			p.setTyp(Typ.ROOT);
			itemRepository.save(p);
			polozka = itemRepository.findByTyp(Typ.ROOT);
		}
        return ItemToSkupina(polozka.get());
    }

	public List<BreadCrumb> seznamSkupin() {
		List<BreadCrumb> skupiny = new ArrayList<>();

		skupiny.add(ItemToBreadCrumb(itemRepository.findByTyp(Typ.ROOT).get()));
		for (Item Item : itemRepository.findAllByTyp(Typ.GROUP)) {
			BreadCrumb p = new BreadCrumb();
			p.setId(Item.getId());
			p.setName(Item.getName());
			p.setTyp(Item.getTyp());
			skupiny.add(p);
		}
		return skupiny;
	}

	public List<Group> seznamPodskupin(Integer idNadrizene) {
		Item nadrizena = itemRepository.getById(idNadrizene);
		List<Group> skupiny = new ArrayList<>();
		for (Item Item : itemRepository.findByParentGroupAndTyp(nadrizena, Typ.GROUP)) {
			skupiny.add(ItemToSkupina(Item));
		}
		return skupiny;
	}

	public List<Representative> seznamZastupcu(Integer idNadrizene) {
		Item nadrizena = itemRepository.getById(idNadrizene);
		List<Representative> representative = new ArrayList<>();
		for (Item Item : itemRepository.findByParentGroupAndTyp(nadrizena, Typ.REPRESENTATIVE)) {
			representative.add(ItemToRepresentative(Item));
		}
		return representative;
	}

	public Integer vytvoritSkupinu(Integer idParentGroup, String nazev, String text) {

		Item nadrizena = itemRepository.getById(idParentGroup);

		Item p = new Item();
		p.setName(nazev);
		p.setText(text);
		p.setTyp(Typ.GROUP);
		p.setParentGroup(nadrizena);
		itemRepository.save(p);
		return p.getId();
	}

	public Integer ulozSkupinu(Integer idParentGroup, Integer idSkupiny, String nazev, String text) {

		Item nadrizena = itemRepository.getById(idParentGroup);
		Item skupina = itemRepository.getById(idSkupiny);
		skupina.setName(nazev);
		skupina.setText(text);
		skupina.setTyp(Typ.GROUP);
		skupina.setParentGroup(nadrizena);
		itemRepository.save(skupina);
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
		if (!(itemRepository.getById(representativeId).getTyp() == Typ.ROOT)) {
			itemRepository.deleteById(representativeId);
		}
	}

	public Integer vytvoritRepresentative(Integer idParentGroup, String name, String name2, String author, String color,
			String text) {

		Item nadrizena = itemRepository.getById(idParentGroup);
		Item representative = new Item(name, name2, author, color, text, Typ.REPRESENTATIVE, nadrizena);

		itemRepository.save(representative);
		return representative.getId();
	}

	public Integer ulozRepresentative(Integer idParentGroup, Integer groupId, String name, String name2,
			String author, String color, String text) {

		Item nadrizena = itemRepository.getById(idParentGroup);
		Item representative = itemRepository.getById(groupId);
		representative.setName(name);
		representative.setName2(name2);
		representative.setAuthor(author);
		representative.setColor(color);
		representative.setText(text);

		representative.setTyp(Typ.REPRESENTATIVE);
		representative.setParentGroup(nadrizena);
		itemRepository.save(representative);
		return representative.getId();
	}

	public Group najdiSkupinuDleId(Integer idSkupiny) {
		Item Item = itemRepository.getById(idSkupiny);
		return ItemToSkupina(Item);
	}

	public Representative najdiRepresentativeDleId(Integer idRepresentative) {
		Item Item = itemRepository.getById(idRepresentative);
		return ItemToRepresentative(Item);
	}

	public Image najdiImageDleId(Integer id) {
		return imageRepository.getById(id);
	}

	public Group ItemToSkupina(Item Item) {
		Group s = new Group();
		s.setId(Item.getId());
		s.setName(Item.getName());
		s.setText(Item.getText());
		if (Item.getParentGroup() != null) {
			s.setIdParentGroup(Item.getParentGroup().getId());
		}

		return s;
	}

	public Representative ItemToRepresentative(Item item) {
		Representative r = new Representative();
		r.setId(item.getId());
		r.setName(item.getName());
		r.setName2(item.getName2());
		r.setAuthor(item.getAuthor());
		r.setColor(item.getColor());
		r.setText(item.getText());
		if (item.getParentGroup() != null) {
			r.setIdParentGroup(item.getParentGroup().getId());
		}
		if (item.getImages() != null) {
			for (Image Image : item.getImages()) {
				r.getImages().add(PhotoFromImage(Image));
			}
		}

		return r;
	}

	public Photo PhotoFromImage(Image Image) {
		Photo f = new Photo();
		f.setId(Image.getId());
		f.setName(Image.getFileName());
		f.setUrl("/image/" + Image.getId());
		return f;
	}

	public BreadCrumb ItemToBreadCrumb(Item Item) {
		BreadCrumb p = new BreadCrumb();
		p.setId(Item.getId());
		p.setName(Item.getName());
		p.setTyp(Item.getTyp());
		return p;
	}

	public void uploadImage(Integer ItemId, MultipartFile file) {

		Image o = new Image();
		o.setFileName(file.getOriginalFilename());
		Item p = itemRepository.getById(ItemId);
		o.setItem(p);
		imageRepository.save(o);
		log.info("Uploading file " + file.getOriginalFilename());
		try {
			cestaKObrazkum = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/images").getAbsolutePath();
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

		Item p = itemRepository.getById(ItemId);

		Image o = new Image();
		o.setItem(p);
		o.setFileName(file.getName());

		imageRepository.save(o);
		log.info("Importing file " + file.getAbsolutePath());
		try {
			cestaKObrazkum = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/images").getAbsolutePath();
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
		Image o = imageRepository.getById(ImageId);
		cestaKObrazkum = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/images").getAbsolutePath();
		return new File(cestaKObrazkum, String.valueOf(o.getId()));
	}

	public void deleteImage(Integer id, Integer ImageId) {
		Image o = imageRepository.getById(ImageId);
		cestaKObrazkum = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/images").getAbsolutePath();
		File f = new File(cestaKObrazkum, String.valueOf(o.getId()));
		f.delete();

		imageRepository.delete(o);

	}

	// rekurzivni funkce pro zanoreni do podpolozek
	public TransportItem ItemToTP(Integer idPolozky) {
		TransportItem tp = new TransportItem();
		Item Item = itemRepository.getById(idPolozky);

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
		for (Item podItem : itemRepository.getById(idPolozky).getItems()) {
			rekurzivniPridavaniPolozek(podItem.getId(), database);
		}
	}

	public void pridatDoTransportItem(Integer idPolozky, List<TransportItem> listTP) {
		TransportItem tp = ItemToTP(idPolozky);
		listTP.add(tp);
	}

	public void pridavaniObrazku(List<TransportImage> obrazky) {
		List<Image> obrazkyList = imageRepository.findAll();
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
		Image o = imageRepository.getById(ImageId);
		cestaKObrazkum = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/images").getAbsolutePath();
		File f = new File(cestaKObrazkum, String.valueOf(o.getId()));
		return new FileInputStream(f);
	}

	public List<BreadCrumb> getBreadCrumbs(Integer id) {
		List<BreadCrumb> bcList = new ArrayList<>();
		Item pom = itemRepository.getById(id);
		while (pom != null) {
			BreadCrumb b = new BreadCrumb();
			b.setId(pom.getId());
			b.setName(pom.getName());
			b.setTyp(pom.getTyp());
			bcList.add( b);
			pom = pom.getParentGroup();
		}
		List<BreadCrumb> shallowCopy = bcList.subList(0, bcList.size());
		Collections.reverse(shallowCopy);
		return shallowCopy;
	}
}
