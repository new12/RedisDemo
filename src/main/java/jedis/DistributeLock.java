package jedis;

import org.joda.time.DateTime;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

/**
 * Created by kylong on 2016/9/11.
 */
public class DistributeLock {

    private RedisTemplate redisTemplate;

    private UUID indentifier;

    public Boolean acquireLock(final String lockName, final int timeOut){
        return  (Boolean)redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return acquireLock(connection, lockName, timeOut);
            }
        });
    }

    private boolean acquireLock(RedisConnection conn, String lockName, int acquireTimeOut){
        DateTime now = DateTime.now();
        DateTime end = now.plusSeconds(acquireTimeOut);
        while(end.isAfterNow()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            indentifier = UUID.randomUUID();
            if (conn.setNX(lockName.getBytes(), indentifier.toString().getBytes())){
                return  true;
            }
        }
        return  false;
    }

    private void releaseLock(RedisConnection connection, String lockName){
        connection.watch(lockName.getBytes());
        byte[] remoteIndentifier = connection.get(lockName.getBytes());
        if (new String(remoteIndentifier).equals(indentifier.toString())){
            connection.multi();
            connection.del(lockName.getBytes());
            connection.exec();
        }else {
            connection.unwatch();
        }
    }

    public void releaseLock( final String lockName){
        redisTemplate.execute(new RedisCallback() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                releaseLock(connection, lockName);
                return null;
            }
        });
    }

    private  boolean accquireLock(RedisConnection conn, String lockName, int acquireTimeOut,int lockTimeOut){
        DateTime now = DateTime.now();
        DateTime end = now.plusSeconds(acquireTimeOut);
        while(end.isAfterNow()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            indentifier = UUID.randomUUID();
            if (conn.setNX(lockName.getBytes(), indentifier.toString().getBytes())){
                conn.expire(lockName.getBytes(), lockTimeOut);
                return  true;
            }else  if (conn.ttl(lockName.getBytes())==-2){
                conn.expire(lockName.getBytes(), lockTimeOut);
            }
        }
        return  false;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
