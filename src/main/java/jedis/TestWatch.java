package jedis;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by kylong on 2016/9/9.
 */
public class TestWatch {

    public static void listItem(RedisConnection conn, String itemId, String sellerId, double price){
        String inventory = String.format("inventory:%s", sellerId);
        String item = String.format("%s.%s",itemId, sellerId);
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            conn.watch(inventory.getBytes());
            if (!conn.sIsMember(inventory.getBytes(), itemId.getBytes())) {
                conn.unwatch();
                continue;
            }
            conn.multi();
            conn.zAdd("market".getBytes(), price, item.getBytes());
            conn.sRem(inventory.getBytes(), itemId.getBytes());
            System.out.println(conn.exec());
        }
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jedis.xml");
        final RedisTemplate redisTemplate = applicationContext.getBean("jedisTemplate", RedisTemplate.class);
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.execute(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                purchaseItem(connection, "16", "ItemQ","17",91);
                return null;
            }
        });
    }

    public static void purchaseItem(RedisConnection conn, String buyerId, String itemId, String sellerId, double lPrice){
        String buyer = String.format("users:%s", buyerId);
        String seller = String.format("users:%s",sellerId);
        String item = String.format("%s.%s",itemId, sellerId);
        String inventory = String.format("inventory:%s",buyerId);
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            conn.watch("market".getBytes(), buyer.getBytes());
            Double price = conn.zScore("market".getBytes(), item.getBytes());
            Double funds = Double.parseDouble(new String(conn.hGet(buyer.getBytes(), "funds".getBytes())));
            if (price!=lPrice || price>funds){
                return;
            }
            conn.multi();
            conn.hIncrBy(seller.getBytes(), "funds".getBytes(), price);
            conn.hIncrBy(buyer.getBytes(),"funds".getBytes(), -price);
            conn.sAdd(inventory.getBytes(),itemId.getBytes());
            conn.zRem("market".getBytes(),item.getBytes());
            conn.exec();

        }
    }
}
