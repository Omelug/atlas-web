package cz.gymtrebon.zaverecky.vjanecek.atlas.service;

import cz.gymtrebon.zaverecky.vjanecek.atlas.entity.User;
import cz.gymtrebon.zaverecky.vjanecek.atlas.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void updateLastLogin(User user) {
        user.setLastLogin(new Date());
        userRepository.save(user);
    }
}