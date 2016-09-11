package jedis;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

/**
 * Created by kylong on 2016/9/9.
 */
public class TestExpire {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jedis.xml");
        final RedisTemplate<String, Long> redisTemplate = applicationContext.getBean("jedisTemplate", RedisTemplate.class);
        ValueOperations<String, Long> value = redisTemplate.opsForValue();
        value.set("my",1l);
        redisTemplate.expire("my",20, TimeUnit.SECONDS);
    }
}
