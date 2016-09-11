package jedis;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by kylong on 2016/9/7.
 */
public class TestTransaction {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jedis.xml");
        final RedisTemplate<String, Long> redisTemplate = applicationContext.getBean("jedisTemplate", RedisTemplate.class);
        redisTemplate.setEnableTransactionSupport(true);
        final CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 5; i++) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                    }
                    redisTemplate.execute(new RedisCallback<Object>() {
                        public Object doInRedis(RedisConnection connection) throws DataAccessException {
                            connection.multi();
                            connection.incrBy("incre".getBytes(), 1l);
                            connection.exec();
                            return null;
                        }
                    });
//                    redisTemplate.multi();
//                    ValueOperations<String, Long> value = redisTemplate.opsForValue();
//                    value.increment("incre",1l);
//                    System.out.println(new Date() + " " + value.get("incre"));
//                    redisTemplate.exec();
                }
            }.start();
        }
        latch.countDown();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
