package my.seckill;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @date 2022/10/03
 */
@Component
public class UserService {
    private static final Map<String, String> usersAndPasswords = new HashMap<>();

    static {
        usersAndPasswords.put("asd", "123");
    }

    public boolean checkUserAndPassword(String user, String password) {
        return Objects.equals(usersAndPasswords.get(user), password);
    }
}
