package com.hgits.task;

/**
 * 自定义任务
 *
 * @author Wang Guodong
 */
public class MyTask implements Runnable {

    public MyTask() {
    }
    private boolean alive = true;//任务运行标识

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public void run() {

    }
}
