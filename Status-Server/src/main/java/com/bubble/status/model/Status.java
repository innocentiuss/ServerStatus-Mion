package com.bubble.status.model;

import lombok.Data;

@Data
public class Status {
    private Long now;
    private String uptime;
    private Double load1;
    private Double load5;
    private Double load15;
    private String memory_total;
    private String memory_used;
    private String swap_total;
    private String swap_used;
    private String hdd_total;
    private String hdd_used;
    private String cpu;
    private Integer network_rx;
    private Integer network_tx;
    private Long network_in;
    private Long network_out;
    private String ip_status;
    private Double loss_10010;
    private Double loss_189;
    private Double loss_10086;
    private Integer ping_10010;
    private Integer ping_189;
    private Integer ping_10086;
    private Integer tcp;
    private Integer udp;
    private Integer process;
    private Integer thread;
    private String ioRead;
    private String ioWrite;
}
