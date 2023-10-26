package com.ezreal.rpc.core.common;

import java.util.concurrent.Semaphore;

/**
 * @author Ezreal
 * @Date 2023/10/25
 */
public class ServerServiceSemaphoreWrapper {

    private Semaphore semaphore;

    private int maxNum;

    public ServerServiceSemaphoreWrapper(int maxNum) {
        this.maxNum = maxNum;
        semaphore = new Semaphore(maxNum);
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }
}
