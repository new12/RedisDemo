package jedis;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kylong on 2016/9/7.
 */
public class TestConnection {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jedis.xml");
        RedisTemplate<String,Long> redisTemplate = applicationContext.getBean("jedisTemplate",RedisTemplate.class);
        ValueOperations<String, Long> value = redisTemplate.opsForValue();
        ListOperations<String, Long> list = redisTemplate.opsForList();
        SetOperations<String, Long> set = redisTemplate.opsForSet();
        String key = "incre";
        value.set(key,123l);
        value.increment(key,1);
        System.out.println(value.get(key));

        String k1 = "aaa";
        System.out.println(list.rightPush(k1,13l));
        System.out.println(list.rightPush(k1,15l));
        System.out.println(list.range(k1,0,-1));


        String k2 = "bbb";
        System.out.println(set.add(k2,1l,2l,3l));

        applicationContext.close();
    }

}
