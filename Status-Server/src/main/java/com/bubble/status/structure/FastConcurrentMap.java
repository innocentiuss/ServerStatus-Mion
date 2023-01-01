package com.bubble.status.structure;

import com.bubble.status.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

// 自定义数据结构 暂时无用
public class FastConcurrentMap {

    private static final Logger LOG = LoggerFactory.getLogger(FastConcurrentMap.class);

    /**
     * Default capacity of the internal array
     */
    private static final int DEFAULT_ARRAY_CAP = 0x1 << 5;

    private AtomicReferenceArray<Status> elements;

    private static volatile FastConcurrentMap instance;

    public static FastConcurrentMap getInstance() {
        if (null == instance) {
            synchronized (FastConcurrentMap.class) {
                if (null == instance) {
                    instance = new FastConcurrentMap();
                }
            }
        }
        return instance;
    }
    // 私有构造方法 防止new
    private FastConcurrentMap() {
        elements = new AtomicReferenceArray<>(DEFAULT_ARRAY_CAP);
    }

    private static final AtomicInteger globalIndex = new AtomicInteger();

    public static int getNextIndex() {
        while (true) {
            int index = globalIndex.getAndIncrement();
            if (index < DEFAULT_ARRAY_CAP) return index;
            else {
                synchronized (globalIndex) {
                    globalIndex.set(0);
                }
            }
        }
    }

    public int insertVal(int index, Status val) {
        checkIndex(index);
        while (true) {
            boolean success = elements.compareAndSet(index, null, val);
            if (success) return index;

            index = getNextIndex();
        }
    }

    public void updateVal(int index, Status val) {
        checkIndex(index);
        elements.set(index, val);
    }

    public Status getVal(int index) {
        checkIndex(index);
        return elements.get(index);
    }

    private void checkIndex(int index) {
        if (index >= elements.length()) throw new IllegalArgumentException("index error");

    }


}
