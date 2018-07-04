package com.hxlx.core.lib.utils.android.thread;

import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 线程任务队列
 *
 * thread pool executor
 */
public class ThreadPoolManager implements Executor {

    private static final String TAG = ThreadPoolManager.class.getSimpleName();
    /**
     * debug mode turn
     */
    private boolean debug = false;

    private static final int CPU_CORE = CPUInfoUtil.getCoresNumbers();
    private static final int DEFAULT_CACHE_SENCOND = 5;
    private static ThreadPoolExecutor threadPool;
    private int coreSize = CPU_CORE;
    private int queueSize = coreSize * 32;
    private final Object lock = new Object();
    private LinkedList<WrappedRunnable> runningList = new LinkedList<WrappedRunnable>();
    private LinkedList<WrappedRunnable> waitingList = new LinkedList<WrappedRunnable>();
    private SchedulePolicy schedulePolicy = SchedulePolicy.FirstInFistRun;
    private OverloadPolicy overloadPolicy = OverloadPolicy.DiscardOldTaskInQueue;


    /**
     * 等待队列调度策略
     */
    public enum SchedulePolicy {
        LastInFirstRun, //后进先执行
        FirstInFistRun  //先进先执行
    }


    /**
     * 过载策略
     */
    public enum OverloadPolicy {
        DiscardNewTaskInQueue, //把等待队列中的 最后一个任务抛弃掉。
        DiscardOldTaskInQueue, //把等待队列中的 第一个任务抛弃掉
        DiscardCurrentTask,  // 直接抛弃当前任务
        CallerRuns, //直接在当前线程中运行。
        ThrowExecption //抛出异常
    }

    public ThreadPoolManager() {
        initThreadPool();
    }

    public ThreadPoolManager(int coreSize, int queueSize) {
        this.coreSize = coreSize;
        this.queueSize = queueSize;
        initThreadPool();
    }

    protected synchronized void initThreadPool() {
        if (debug) {
            Log.v(TAG, "ThreadPoolManager core-queue size: " + coreSize + " - " + queueSize
                    + "  running-wait task: " + runningList.size() + " - " + waitingList.size());
        }
        if (threadPool == null) {
            threadPool = createDefaultThreadPool();
        }
    }

    public static ThreadPoolExecutor createDefaultThreadPool() {
        // 控制最多4个keep在pool中
        int corePoolSize = Math.min(4, CPU_CORE);
        return new ThreadPoolExecutor(
                corePoolSize,
                Integer.MAX_VALUE,
                DEFAULT_CACHE_SENCOND, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactory() {
                    static final String NAME = "lite-";
                    AtomicInteger IDS = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, NAME + IDS.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     * turn on or turn off debug mode
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public static void setThreadPool(ThreadPoolExecutor threadPool) {
        ThreadPoolManager.threadPool = threadPool;
    }

    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public boolean cancelWaitingTask(Runnable command) {
        boolean removed = false;
        synchronized (lock) {
            int size = waitingList.size();
            if (size > 0) {
                for (int i = size - 1; i >= 0; i--) {
                    if (waitingList.get(i).getRealRunnable() == command) {
                        waitingList.remove(i);
                        removed = true;
                    }
                }
            }
        }
        return removed;
    }

    interface WrappedRunnable extends Runnable {

        Runnable getRealRunnable();
    }

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask<T>(runnable, value);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new FutureTask<T>(callable);
    }

    /**
     * submit runnable
     * 可cancel掉
     */
    public Future<?> submit(Runnable task) {
        RunnableFuture<Void> ftask = newTaskFor(task, null);
        execute(ftask);
        return ftask;
    }

    /**
     * submit Runnable
     *
     * @param task
     * @param result
     * @param <T>
     * @return
     */
    public <T> Future<T> submit(Runnable task, T result) {
        RunnableFuture<T> ftask = newTaskFor(task, result);
        execute(ftask);
        return ftask;
    }

    /**
     * submit callable
     */
    public <T> Future<T> submit(Callable<T> task) {
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }


    /**
     * submit RunnableFuture task
     */
    public <T> void submit(RunnableFuture<T> task) {
        execute(task);
    }


    /**
     * 执行
     *
     * @param command
     */
    @Override
    public void execute(final Runnable command) {
        if (command == null) {
            return;
        }

        WrappedRunnable scheduler = new WrappedRunnable() {
            @Override
            public Runnable getRealRunnable() {
                return command;
            }

            @Override
            public void run() {
                try {
                    command.run();
                } finally {
                    scheduleNext(this);
                }
            }
        };

        boolean callerRun = false;
        synchronized (lock) {
            if (runningList.size() < coreSize) {
                runningList.add(scheduler);
                threadPool.execute(scheduler);
            } else if (waitingList.size() < queueSize) {
                waitingList.addLast(scheduler);
            } else {
                switch (overloadPolicy) {
                    case DiscardNewTaskInQueue:
                        waitingList.pollLast();
                        waitingList.addLast(scheduler);
                        break;
                    case DiscardOldTaskInQueue:
                        waitingList.pollFirst();
                        waitingList.addLast(scheduler);
                        break;
                    case CallerRuns:
                        callerRun = true;
                        break;
                    case DiscardCurrentTask:
                        break;
                    case ThrowExecption:
                        throw new RuntimeException(
                                "Task rejected from lite smart executor. " + command.toString());
                    default:
                        break;
                }
            }
            //printThreadPoolInfo();
        }
        if (callerRun) {
            if (debug) {
                Log.i(TAG, "ThreadPoolManager task running in caller thread");
            }
            command.run();
        }
    }

    private void scheduleNext(WrappedRunnable scheduler) {
        synchronized (lock) {
            boolean suc = runningList.remove(scheduler);

            if (!suc) {
                runningList.clear();
            }
            if (waitingList.size() > 0) {
                WrappedRunnable waitingRun;
                switch (schedulePolicy) {
                    case LastInFirstRun:
                        waitingRun = waitingList.pollLast();
                        break;
                    case FirstInFistRun:
                        waitingRun = waitingList.pollFirst();
                        break;
                    default:
                        waitingRun = waitingList.pollLast();
                        break;
                }
                if (waitingRun != null) {
                    runningList.add(waitingRun);
                    threadPool.execute(waitingRun);
                    Log.v(TAG,
                            "Thread " + Thread.currentThread().getName() + " execute next task..");
                } else {
                    Log.e(TAG,
                            "ThreadPoolManager get a NULL task from waiting queue: "
                                    + Thread.currentThread().getName());
                }
            } else {
                if (debug) {
                    Log.v(TAG, "ThreadPoolManager: all tasks is completed. current thread: " +
                            Thread.currentThread().getName());
                    //printThreadPoolInfo();
                }
            }
        }
    }

    public void printThreadPoolInfo() {
        if (debug) {
            Log.i(TAG, "___________________________");
            Log.i(TAG, "state (shutdown - terminating - terminated): " + threadPool.isShutdown()
                    + " - " + threadPool.isTerminating() + " - " + threadPool.isTerminated());
            Log.i(TAG, "pool size (core - max): " + threadPool.getCorePoolSize()
                    + " - " + threadPool.getMaximumPoolSize());
            Log.i(TAG, "task (active - complete - total): " + threadPool.getActiveCount()
                    + " - " + threadPool.getCompletedTaskCount() + " - "
                    + threadPool.getTaskCount());
            Log
                    .i(TAG, "waitingList size : " + threadPool.getQueue().size() + " , "
                            + threadPool.getQueue());
        }
    }

    public int getCoreSize() {
        return coreSize;
    }

    public int getRunningSize() {
        return runningList.size();
    }

    public int getWaitingSize() {
        return waitingList.size();
    }

    /**
     * 设置最大并发任务数
     *
     * @param coreSize number of concurrent tasks at the same time
     * @return this
     */
    public ThreadPoolManager setCoreSize(int coreSize) {
        if (coreSize <= 0) {
            throw new NullPointerException("coreSize can not <= 0 !");
        }
        this.coreSize = coreSize;
        if (debug) {
            Log.v(TAG, "ThreadPoolManager core-queue size: " + coreSize + " - " + queueSize
                    + "  running-wait task: " + runningList.size() + " - " + waitingList.size());
        }
        return this;
    }

    public int getQueueSize() {
        return queueSize;
    }

    /**
     * 设置最大等待队列数
     *
     * @param queueSize waiting queue size
     * @return this
     */
    public ThreadPoolManager setQueueSize(int queueSize) {
        if (queueSize < 0) {
            throw new NullPointerException("queueSize can not < 0 !");
        }

        this.queueSize = queueSize;
        if (debug) {
            Log.v(TAG, "ThreadPoolManager core-queue size: " + coreSize + " - " + queueSize
                    + "  running-wait task: " + runningList.size() + " - " + waitingList.size());
        }
        return this;
    }


    public OverloadPolicy getOverloadPolicy() {
        return overloadPolicy;
    }

    public void setOverloadPolicy(OverloadPolicy overloadPolicy) {
        if (overloadPolicy == null) {
            throw new NullPointerException("OverloadPolicy can not be null !");
        }
        this.overloadPolicy = overloadPolicy;
    }

    public SchedulePolicy getSchedulePolicy() {
        return schedulePolicy;
    }

    public void setSchedulePolicy(SchedulePolicy schedulePolicy) {
        if (schedulePolicy == null) {
            throw new NullPointerException("SchedulePolicy can not be null !");
        }
        this.schedulePolicy = schedulePolicy;
    }

}
