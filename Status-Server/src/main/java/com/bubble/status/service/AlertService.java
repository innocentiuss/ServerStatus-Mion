package com.bubble.status.service;

import com.bubble.status.model.Status;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AlertService {

    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 10, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    // key: Server => key: quota -> value: abnormal status
    Map<String, Map<String, Queue<Status>>> serverQuotaStatus = new ConcurrentHashMap<>();

    public void judge(String server, Status status) {
        threadPool.execute(() -> {
            Map<String, Queue<Status>> quotas;
            if ((quotas = serverQuotaStatus.get(server)) == null) {
                quotas = new ConcurrentHashMap<>();
                serverQuotaStatus.put(server, quotas);
            }


        });
    }

    private void checkRam(Status status) {

    }

    private void checkLoad(Status status) {

    }

    private void checkCPU(Status status) {

    }
}
