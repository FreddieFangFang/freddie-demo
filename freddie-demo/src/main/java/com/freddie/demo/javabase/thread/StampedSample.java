package com.freddie.demo.javabase.thread;


import javax.xml.crypto.Data;
import java.util.Date;
import java.util.concurrent.locks.StampedLock;

public class StampedSample {
    private final StampedLock sl = new StampedLock();

    void mutate() {
        long stamp = sl.writeLock();
        try {
            write();
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    private void write() {
        //do something
    }

    Data access() {
        long stamp = sl.tryOptimisticRead();
        Data data = read();
        if (!sl.validate(stamp)) {
            stamp = sl.readLock();
            try {
                data = read();
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return data;
    }

    private Data read() {
        //do something
        return null;
    }
    // …

}

