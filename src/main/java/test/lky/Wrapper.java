package test.lky;

import java.util.Map;
import java.util.Random;

/**
 * Created by kylong on 2016/9/8.
 */
public class Wrapper {
    public static void main(String[] args) {
        Map map = System.getProperties();
        Random r = new Random();
        while(true){
            map.put(r.nextInt(), new byte[1000]);
        }
    }
}
