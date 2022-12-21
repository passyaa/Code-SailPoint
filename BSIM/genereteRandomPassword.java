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

return pwd;