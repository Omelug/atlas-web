package cz.gymtrebon.zaverecky.vjanecek.atlas.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptMigrate {
        public static void main(String[] args) {
            // Connect to your database and fetch the old hashed passwords
            // Iterate over each old password and migrate it to BCrypt
            // Update the user's password field with the new BCrypt hash

            PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            // Example migration for an old password
            String oldHashedPassword = "admin";
            String newHashedPassword = bCryptPasswordEncoder.encode(oldHashedPassword);
            System.out.println(newHashedPassword);

            // Update the user's password field in the database with newHashedPassword
            // Repeat this process for all users with old hashed passwords
        }
}
