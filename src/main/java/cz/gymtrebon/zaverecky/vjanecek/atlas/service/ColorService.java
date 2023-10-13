package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Color;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ColorRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ColorService {
    private final ItemRepository itemRepository;
    private final ColorRepository colorRepository;

    public Set<Color> getColorsObject(List<String> colors) {
        Set<Color> colors_object = new HashSet<>();
        if (colors==null) return new HashSet<>();
        for (String color : colors) {
            Optional<Color> colorOptional= colorRepository.findByName(color);
            colorOptional.ifPresent(colors_object::add);
        }
        return colors_object;
    }

    public static List<String> colorsToString(Set<Color> colors) {
        List<String> colorsString = new ArrayList<>();
        for (Color color : colors) {
            colorsString.add(color.getName());
        }
        return colorsString;
    }
}
