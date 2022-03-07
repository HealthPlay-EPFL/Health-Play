package ch.epfl.sdp.healthplay.model;

import org.junit.Test;

public class ProductInfoClientTest {
    @Test
    public void test1() {
        String p =  ProductInfoClient.getInfo("737628064502");
        System.out.println(p);
        System.out.println("asfasdfasdf");
    }
}
