package com.bubble.status.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Result {
    private List<ServerInfoVo> servers;
    private Integer updated;
}
