package ru.sofitlabs.chat.common.utils.thread;

import javax.annotation.Nonnull;
import java.util.concurrent.*;

/**
 * Author:      daa
 * Date:        05.04.16
 * Company:     SofIT labs
 */
public abstract class ThreadRunner {

    @Nonnull
    private final Object threadSync = new Object();

    /**
     * Очередь обработки запросов
     */
    @Nonnull
    private final LinkedBlockingQueue<Runnable> runnables = new LinkedBlockingQueue<>();

    /**
     * Счётчик количества тредов
     */
    private volatile int threadCounter = 0;

    /**
     * Фабрика тредов
     */
    @Nonnull
    private final ThreadFactory threadFactory =
            r -> {
                final Thread thread = Executors.defaultThreadFactory().newThread(r);
                int n;
                synchronized (threadSync) {
                    n = threadCounter++;
                }
                thread.setName(getThreadName() + "-" + n);
                return thread;
            };

    /**
     * Тред пул
     */
    @Nonnull
    private final ThreadPoolExecutor executorService = new ThreadPoolExecutor(4, 20, 60, TimeUnit.SECONDS,
            runnables, threadFactory);

    /**
     * @return имя потока
     */
    @Nonnull
    public abstract String getThreadName();

    @Nonnull
    public ThreadPoolExecutor getExecutorService() {
        return executorService;
    }
}
