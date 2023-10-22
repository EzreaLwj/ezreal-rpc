package com.ezreal.rpc.core.spi.jdk;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author Ezreal
 * @Date 2023/10/22
 */
public class TestDemo {


    private static void doTest(ISpiTest iSpiTest) {
        System.out.println("begin");
        iSpiTest.doTest();
        System.out.println("end");
    }

    public static void main(String[] args) {

        ServiceLoader<ISpiTest> serviceLoader = ServiceLoader.load(ISpiTest.class);
        Iterator<ISpiTest> iterator = serviceLoader.iterator();

        while (iterator.hasNext()) {
            ISpiTest iSpiTest = iterator.next();

            doTest(iSpiTest);
        }

    }
}
