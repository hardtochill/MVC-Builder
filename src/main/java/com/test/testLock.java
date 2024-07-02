package com.test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class testLock {
    public static Lock lock = new ReentrantLock();
    public static Integer synLock = 0;
    public void testSyn(){
        Thread synT = new Thread(() -> {
            synchronized (synLock){
                try {
                    wait();
                    //Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        synT.start();
        synT.interrupt();
    }
    public void testLock(){
        Thread thread = new Thread(()->{
            try {
                lock.lockInterruptibly();
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                lock.unlock();
            }
        });
        thread.interrupt();
    }
    public static void main(String[] args) {
        new testLock().testLock();
    }
}
