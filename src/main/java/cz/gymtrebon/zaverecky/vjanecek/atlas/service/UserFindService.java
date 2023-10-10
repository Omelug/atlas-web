package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.UserFind;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserFindRepository;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserFindService {
  private final UserFindRepository userFindRepository;
  private final UserRepository userRepository;

  public UserFind getUserFind(String name) {
    Optional<UserFind> userFind = userFindRepository.findByUserName(name);
    if (userFind.isEmpty()){
      Optional<User> user = userRepository.findByName(name);
      return user.map(UserFind::new).orElse(null);
    }
    return userFind.get();
  }

  public void setOpen(String name, boolean open) {
    Optional<UserFind> userFind = userFindRepository.findByUserName(name);
    if (userFind.isPresent()){
      UserFind u = userFind.get();
      u.setOpen(open);
      userFindRepository.save(u);
    }
  }
}
