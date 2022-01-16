package org.redisson.example;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redission实现的分布式锁
 */
public class RedissionLock {

    RedissonClient redissonClient;// 锁客户端
    RLock test; // 测试锁

    // 饿汉式单例模式
    private static RedissionLock redissionLock = new RedissionLock();

    /**
     * 实例化Redisson客户端
     */
    private RedissionLock() {
        // 实例化RedissionClient
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(0);
        redissonClient = Redisson.create(config);
        test = redissonClient.getLock("test");
    }

    public static void main(String[] args) {

        try {
            boolean lock = redissionLock.lock(1000, 100, TimeUnit.MILLISECONDS);
            if (lock){
                // 获取到锁
                System.out.println("成功获取到锁～");

            }
        } catch (InterruptedException e) {
            System.out.println("锁获取失败！" + e.getMessage());
        } finally {
            redissionLock.unlock();
        }

    }

    /**
     * 获取锁
     *
     * @param waitTime
     * @param leaseTime
     * @param unit
     * @return boolean
     * @throws InterruptedException
     */
    public boolean lock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        return test.tryLock(waitTime, leaseTime, unit);
    }

    /**
     * 释放锁
     */
    public void unlock() {
        test.unlock();
    }


}
