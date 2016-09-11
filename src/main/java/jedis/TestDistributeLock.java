package jedis;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by kylong on 2016/9/11.
 */
public class TestDistributeLock {
    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        final String lockName = "myLock";
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jedis.xml");
        final DistributeLock distributeLock = applicationContext.getBean("distributeLock", DistributeLock.class);
        if (distributeLock.acquireLock(lockName,80)){
            System.out.println(new Date() + " " + Thread.currentThread().getName() + " get lock");
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
            }
            distributeLock.releaseLock(lockName);
            System.out.println(new Date() + " " +Thread.currentThread().getName() + "  release");

        }else {
            System.out.println(new Date() + " " +Thread.currentThread().getName() + "  timeout");
        }
    }

}
