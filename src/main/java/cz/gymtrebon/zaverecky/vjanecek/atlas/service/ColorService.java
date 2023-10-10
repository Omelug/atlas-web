package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Color;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ColorService {
    private final ColorRepository colorRepository;

    public Set<Color> getColorsObject(List<String> colors) {
        Set<Color> colors_object = new HashSet<>();
        for (String color : colors) {
            Optional<Color> colorOptional= colorRepository.findByName(color);
            colorOptional.ifPresent(colors_object::add);
        }
        return colors_object;
    }
}
