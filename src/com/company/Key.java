package com.company;

public class Key {
    private final Object key;
    private final long timeToLive;
    private final long addTime;

    public Key(Object key, long timeout) {
        this.key = key;
        this.timeToLive = System.currentTimeMillis() + timeout;
        this.addTime = System.currentTimeMillis();
    }

    public Key(Object key) {
        this.key = key;
        this.addTime = System.currentTimeMillis();
        this.timeToLive = 150;
    }


    public Object getKey() {
        return key;
    }

    public boolean isLive(long currentTimeMillis) {
        return System.currentTimeMillis() - addTime > timeToLive;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Key other = (Key) obj;
        if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.key != null ? this.key.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Key{" + "key=" + key + '}';
    }
}
