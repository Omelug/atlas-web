package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb.CurrentDatabase;
import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.*;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Color;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Image;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Item;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.enums.Typ;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ColorRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ImageRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtlasService {
	private final ItemRepository itemRepository;
	private final ImageRepository imageRepository;
	private final ColorRepository colorRepository;
	
	@Value("${images.path}")
	private String imagesFolder;
	private static String imagePath;

	public Group findORcreateGroup() {
		Optional<Item> item = itemRepository.findByTyp(Typ.ROOT);
		if(item.isEmpty()) {
			Item resultItem  = new Item();
			resultItem.setParentGroup(null);
			resultItem.setName(CurrentDatabase.getCurrentDatabase());
			resultItem.setTyp(Typ.ROOT);
			itemRepository.save(resultItem);

			List<Color> colorList = new ArrayList<>();
			colorList.add(new Color("black"));
			colorList.add(new Color("white"));
			colorList.add(new Color("purple"));
			colorList.add(new Color("green"));
			colorList.add(new Color("orange"));
			colorList.add(new Color("pink"));
			colorList.add(new Color("yellow"));
			colorList.add(new Color("gray"));
			colorList.add(new Color("silver"));
			colorList.add(new Color("black"));
			colorList.add(new Color("lime"));
			colorList.add(new Color("brown"));
			colorRepository.saveAll(colorList);

			return ItemToGroup(resultItem);
		}else{
			return ItemToGroup(item.get());
		}
    }

	public List<BreadCrumb> breadCrumbList() {
		List<BreadCrumb> groups = new ArrayList<>();
		Item root = itemRepository.findByTyp(Typ.ROOT).orElse(null);
		/*if (root != null) {
			groups.add(ItemToBreadCrumb(root));
		}*/
		for (Item Item : itemRepository.findAllByTyp(Typ.GROUP)) {
			BreadCrumb bc = new BreadCrumb();
			bc.setId(Item.getId());
			bc.setName(Item.getName());
			bc.setTyp(Item.getTyp());
			groups.add(bc);
		}
		return groups;
	}

	public List<Group> subgroupList(Long parentId) {
		Item parent = itemRepository.getById(parentId);
		List<Group> groups = new ArrayList<>();
		for (Item Item : itemRepository.findByParentGroupAndTyp(parent, Typ.GROUP)) {
			groups.add(ItemToGroup(Item));
		}
		return groups;
	}

	public List<Representative> representativeList(Long parentId) {
		Item parent = itemRepository.getById(parentId);
		List<Representative> representative = new ArrayList<>();
		for (Item Item : itemRepository.findByParentGroupAndTyp(parent, Typ.REPRESENTATIVE)) {
			representative.add(ItemToRepresentative(Item));
		}
		return representative;
	}

	public Long createGroup(Long idParentGroup, String name, String text) {

		Item parent = itemRepository.getById(idParentGroup);

		Item item = new Item();
		item.setName(name);
		item.setText(text);
		item.setTyp(Typ.GROUP);
		item.setParentGroup(parent);
		itemRepository.save(item);
		return item.getId();
	}

	public Long saveGroup(Long parentId, Long groupId, String name, String text) {
		Item group = itemRepository.getById(groupId);
		if (group.getTyp() != Typ.ROOT) {
			Item parent = itemRepository.getById(parentId);
			group.setParentGroup(parent);
		}
		group.setName(name);
		group.setText(text);
		group.setTyp(Typ.GROUP);
		itemRepository.save(group);
		return group.getId();
	}

	public void removeGroup(Long groupId) {
		for (Group group : subgroupList(groupId)) {
			removeGroup(group.getId());
		}
		for (Representative representative : representativeList(groupId)) {
			removeItem(representative.getId());
		}
		removeItem(groupId);
	}

	public void removeItem(Long representativeId) {
		if (!(itemRepository.getById(representativeId).getTyp() == Typ.ROOT)) {
			itemRepository.deleteById(representativeId);
		}
	}

	public Long createRepresentative(Long idParentGroup, String name, String name2, String author, Set<Color> colors,
			String text) {

		Item parent = itemRepository.getById(idParentGroup);
		Item representative = new Item(name, name2, author, colors, text, Typ.REPRESENTATIVE, parent);

		itemRepository.save(representative);
		return representative.getId();
	}

	public Long saveRepresentative(Long idParentGroup, Long groupId, String name, String name2,
			String author, Set<Color> colors, String text) {

		Item parent = itemRepository.getById(idParentGroup);
		Item representative = itemRepository.getById(groupId);
		representative.setName(name);
		representative.setName2(name2);
		representative.setAuthor(author);
		representative.setColors(colors);
		representative.setText(text);

		representative.setTyp(Typ.REPRESENTATIVE);
		representative.setParentGroup(parent);
		itemRepository.save(representative);
		return representative.getId();
	}

	public Group findGroupById(Long groupId) {
		return ItemToGroup(itemRepository.getById(groupId));
	}

	public Representative findRepresentativeById(Long idRepresentative) {
		return ItemToRepresentative(itemRepository.getById(idRepresentative));
	}

	public Image findImageById(Long id) {
		return imageRepository.getById(id);
	}

	public Group ItemToGroup(Item item) {
		Group group = new Group();
		group.setId(item.getId());
		group.setName(item.getName());
		group.setText(item.getText());
		group.setRoot(item.getTyp() == Typ.ROOT);
		if (item.getParentGroup() != null) {
			group.setIdParentGroup(item.getParentGroup().getId());
		}
		return group;
	}

	public Representative ItemToRepresentative(Item item) {
		Representative r = new Representative();
		r.setId(item.getId());
		r.setName(item.getName());
		r.setName2(item.getName2());
		r.setAuthor(item.getAuthor());
		r.setColors(item.getColors());
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
		Photo photo = new Photo();
		photo.setId(Image.getId());
		photo.setName(Image.getFileName());
		photo.setUrl("/image/" + Image.getId());
		return photo;
	}

	public BreadCrumb ItemToBreadCrumb(Item Item) {
		BreadCrumb p = new BreadCrumb();
		p.setId(Item.getId());
		p.setName(Item.getName());
		p.setTyp(Item.getTyp());
		return p;
	}

	public void uploadImage(Long ItemId, MultipartFile file) {

		Image o = new Image();
		o.setFileName(file.getOriginalFilename());
		Item p = itemRepository.getById(ItemId);
		o.setItem(p);
		imageRepository.save(o);
		log.info("Uploading file " + file.getOriginalFilename());
		try {
			imagePath = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/images").getAbsolutePath();
			File f = new File(imagePath, String.valueOf(o.getId()));
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			log.error("Error while saving image", e);
		}
	}
	//for uploading start data files
	public void uploadImage(Long ItemId, File file) {

		Item p = itemRepository.getById(ItemId);

		Image o = new Image();
		o.setItem(p);
		o.setFileName(file.getName());

		imageRepository.save(o);
		log.info("Importing file " + file.getAbsolutePath());
		try {
			File imageFile = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/images");
			imagePath = imageFile.getAbsolutePath();
			imageFile.mkdirs();
			File f = new File(imagePath, String.valueOf(o.getId()));
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

	public void uploadRequestImage(Integer imageId, MultipartFile file, String name) {
		try {
			File imageFile = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/user/"+name+"/request/images");
			imageFile.mkdirs();
			File f = new File(imageFile.getAbsolutePath(), String.valueOf(imageId));
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			log.error("Error while saving image", e);
		}
	}

	/*public void uploadImage(Integer ItemId, File file) {

		Item p = itemRepository.getById(ItemId);

		Image o = new Image();
		o.setItem(p);
		o.setFileName(file.getName());

		imageRepository.save(o);
		log.info("Importing file " + file.getAbsolutePath());
		try {
			imagePath = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/images").getAbsolutePath();
			File f = new File(imagePath, String.valueOf(o.getId()));
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
	}*/

	public File imageFile(Long ImageId) {
		Image o = imageRepository.getById(ImageId);
		imagePath = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/images").getAbsolutePath();
		return new File(imagePath, String.valueOf(o.getId()));
	}

	public void deleteImage(Long ImageId) {
		Image o = imageRepository.getById(ImageId);
		imagePath = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/images").getAbsolutePath();
		File f = new File(imagePath, String.valueOf(o.getId()));
		f.delete();
		imageRepository.delete(o);

	}

	public TransportItem ItemToTransportItem(Long itemId) {
		TransportItem tp = new TransportItem();
		Item Item = itemRepository.getById(itemId);

		tp.setName(Item.getName());
		tp.setText(Item.getText());
		tp.setTyp(Item.getTyp());
		try {
			tp.setIdParentGroup(Item.getParentGroup().getId());
		} catch (Exception e) {
			tp.setIdParentGroup(itemRepository.findByTyp(Typ.ROOT).orElseThrow().getId());
			log.info("getParentGroup is NULL");
		}

		tp.setId(Item.getId());
		tp.setAuthor(Item.getAuthor());
		tp.setColors(Item.getColors());
		tp.setName2(Item.getName2());

		return tp;
	}

	public void recursiveItemAdding(Long itemId, List<TransportItem> database) {
		addToTransportItem(itemId, database);
		for (Item podItem : itemRepository.getById(itemId).getItems()) {
			recursiveItemAdding(podItem.getId(), database);
		}
	}

	private void addToTransportItem(Long itemId, List<TransportItem> listTP) {
		TransportItem tp = ItemToTransportItem(itemId);
		listTP.add(tp);
	}

	public void addImages(List<TransportImage> images) {
		List<Image> obrazkyList = imageRepository.findAll();
		for (Image Image : obrazkyList) {
			TransportImage to = ImageToTO(Image);
			images.add(to);
		}
	}

	private TransportImage ImageToTO(Image Image) {
		TransportImage to = new TransportImage();
		to.setId(Image.getId());
		to.setName(Image.getFileName());
		to.setItemId(Image.getItem().getId());
		to.setCreateDate(String.valueOf(Image.getCreateDate()));
		to.setModifyDate(String.valueOf(Image.getModifyDate()));
		return to;
	}

	public InputStream inputStream(Long ImageId) throws FileNotFoundException {
		Image o = imageRepository.getById(ImageId);
		imagePath = new File(imagesFolder+CurrentDatabase.getCurrentDatabase()+"/images").getAbsolutePath();
		File f = new File(imagePath, String.valueOf(o.getId()));
		return new FileInputStream(f);
	}

	public List<BreadCrumb> getBreadCrumbs(Long id) {
		List<BreadCrumb> bcList = new ArrayList<>();
		Item item = itemRepository.getById(id);
		while (item != null) {
			BreadCrumb b = new BreadCrumb();
			b.setId(item.getId());
			b.setName(item.getName());
			b.setTyp(item.getTyp());
			bcList.add( b);
			item = item.getParentGroup();
		}
		List<BreadCrumb> shallowCopy = bcList.subList(0, bcList.size());
		Collections.reverse(shallowCopy);
		return shallowCopy;
	}
}
