package service;

import org.apache.commons.codec.digest.DigestUtils;
import redis.clients.jedis.Jedis;
import util.JedisClient;

public class UserServiceRedisImpl {

    private static final String DB_PREFIX = "u#";

    private final Jedis client;

    public UserServiceRedisImpl() {
        client = JedisClient.getInstance().getClient();
    }

    public void createUser(){}

    public void updateUser(){};

    public boolean authenticate(String username, String password){
        if (!client.exists(DB_PREFIX + username)){
            return false;
        }

        String originalPwdHash = client.get(DB_PREFIX + username);
        String pwdHash = DigestUtils.sha256Hex(password);

        return (originalPwdHash.equals(pwdHash));
    }
}
