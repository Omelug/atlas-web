package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.UDRLink;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UDRLinkService {

    private final UDRLinkRepository udrlinkRepository;

    public List<GrantedAuthority> getAuthorities(User user) {
        List<UDRLink> udrlist = udrlinkRepository.findAllByUser(user);
        Set<String> set = new HashSet<>();
        for (UDRLink udrlink : udrlist){
            set.add(udrlink.getRole().getName());
        }
        return Arrays.stream(set.toArray(new String[set.size()])).
                map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
