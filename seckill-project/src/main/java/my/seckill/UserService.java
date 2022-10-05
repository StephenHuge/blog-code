package my.seckill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @date 2022/10/03
 */
@Component
@Slf4j
public class UserService {
    private static final Map<String, String> usersAndPasswords = new HashMap<>();

    static {
        usersAndPasswords.put("asd", "123");
    }


    public boolean checkUserAndPassword(String user, String password) {
        return Objects.equals(usersAndPasswords.get(user), password);
    }
}
