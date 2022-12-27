import java.security.SecureRandom;
import java.security.*;
import java.math.*;

String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
        
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
                
        for (int i = 0; i < 10; i++) {
           int randomIndex = random.nextInt(characters.length());
           sb.append(characters.charAt(randomIndex));
        }
        
        String pwd = sb.toString();
        System.out.println("Password : "+ pwd);
        
        MessageDigest m = null;
        try {
            m=MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException e) {
            System.out.println("Something is wrong");
        }
            
        m.update(pwd.getBytes(),0,pwd.length());

        String result = new BigInteger(1,m.digest()).toString(16);
        System.out.println("MD5 : "+ result);

return result;