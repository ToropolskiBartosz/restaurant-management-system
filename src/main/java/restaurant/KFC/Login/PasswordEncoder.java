package restaurant.KFC.Login;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {

    public static String coding(String userPassword){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = userPassword;
        String passwordCoding = encoder.encode(password);
        return passwordCoding;
    }
}
