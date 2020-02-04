package com.company;

import java.util.Map;
import java.util.concurrent.*;

public class CacheUtils<K, V> {

    private ConcurrentHashMap<Key, V> globalMap = new ConcurrentHashMap<Key, V>();
    private long timeToLive;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread th = new Thread(r);
            th.setDaemon(true);
            return th;
        }
    });
    /**
     * @param timeToLive - время, которое обьект будет кранится в кеше.
     * @param checkTime - интервал времени для автоматической проверки и удаления "протухщих" элементов.
     * @param timeUnit - константа, которая задает единицы измерения для времени хранения объекта в кеше.
     */
    public CacheUtils(long timeToLive, long checkTime, TimeUnit timeUnit) throws Exception {
        if (timeToLive < 100) {
            throw new Exception("Слишком коротки интервал для хранения");
        }
        this.timeToLive = timeToLive;
        scheduler.scheduleAtFixedRate(new Runnable() {
            /*scheduler пробегается по элементам через каждый временной отрезок checkTime
            и если время хранения истекло, удлаяет объект
            */
            @Override
            public void run() {
                long current = System.currentTimeMillis();
                for (Key k : globalMap.keySet()) {
                    if (!k.isLive(current)) {
                        globalMap.remove(k);
                    }
                }
            }
        }, 1, checkTime, timeUnit);
    }

    public void setTimeToLive(long timeToLive) throws Exception {
        if (timeToLive < 100) {
            throw new Exception("Короткий интервал для хранения");
        }
        this.timeToLive = timeToLive;
    }

    public void put(K key, V data) {
        globalMap.put(new Key(key, timeToLive), data);
    }

    public void put(K key, V data, long timeToLive) {
        globalMap.put(new Key(key, timeToLive), data);
    }

    public V get(K key) {
        return globalMap.get(new Key(key));
    }

    public void remove(K key) {
        globalMap.remove(new Key(key));
    }

    public void removeAll() {
        globalMap.clear();
    }

    public void setAll(Map<K, V> map) {
        ConcurrentHashMap tempmap = new ConcurrentHashMap<Key, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            tempmap.put(new Key(entry.getKey(), timeToLive), entry.getValue());
        }
        globalMap = tempmap;
    }

    public void addAll(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
}