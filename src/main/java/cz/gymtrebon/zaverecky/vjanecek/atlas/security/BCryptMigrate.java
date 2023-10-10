package cz.gymtrebon.zaverecky.vjanecek.atlas.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptMigrate {
        public static void main(String[] args) {
            PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            String oldHashedPassword = "admin";
            String newHashedPassword = bCryptPasswordEncoder.encode(oldHashedPassword);
            System.out.println(newHashedPassword);
        }
}
