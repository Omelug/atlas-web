package cz.gymtrebon.zaverecky.vjanecek.atlas.currentdb;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Data
public class CurrentDatabase{
    @Getter @Setter
    private static String currentDatabase;
}