package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.dto.helpobjects.DatabaseListHelper;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.Database;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.UDRLink;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.DatabaseRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UDRLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UDRLinkService {

    private final UDRLinkRepository udrlinkRepository;
    private final DatabaseRepository databaseRepository;

    public static List<User> findUsersInUDRList(List<UDRLink> udrLinklist) {
        Set<User> userSet = new HashSet<>();
        for (UDRLink udrLink : udrLinklist) {
            userSet.add(udrLink.getUser());
        }
        return new ArrayList<>(userSet);
    }

    public List<DatabaseListHelper> getDatabaseList(String username) {
        List<UDRLink> list = udrlinkRepository.findAllByUserName(username);
        List<DatabaseListHelper> databaseList = new ArrayList<>();
        for (UDRLink udrlink : list) {
            if (udrlink.getRole().getName().equals("ADMIN")) {
                databaseRepository.findAll();
                databaseList = new ArrayList<>();
                for (Database database: databaseRepository.findAll()){
                    databaseList.add(new DatabaseListHelper(database.getName(), "ADMIN"));
                }
                return databaseList;
            }
            boolean found = false;
            for (DatabaseListHelper databaseListHelper: databaseList ) {
                if (databaseListHelper.getDatabase().equals(udrlink.getDatabase().getName())) {
                    databaseListHelper.addRole(udrlink.getRole().getName());
                    found = true;
                }
            }
            if (!found) {
                databaseList.add(new DatabaseListHelper(udrlink.getDatabase().getName(), udrlink.getRole().getName()));
            }
        }
        return databaseList;
    }

    public List<GrantedAuthority> getAuthorities(User user) {
        List<UDRLink> udrlist = udrlinkRepository.findAllByUser(user);
        Set<String> set = new HashSet<>();
        for (UDRLink udrlink : udrlist){
            set.add(udrlink.getRole().getName());
        }
        return set.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public List<UDRLink> getUDRLinks(User user, String databaseName) {
        if (isAdmin(user)) {
            return udrlinkRepository.findAllByUserName(user.getName());
        }
        return udrlinkRepository.findAllByUserNameAndDatabaseName(user.getName(), databaseName);
    }

    public boolean isAdmin(User user) {
        return !udrlinkRepository.findByUserNameAndRoleName(user.getName(),User.ADMIN).isEmpty();
    }
}
