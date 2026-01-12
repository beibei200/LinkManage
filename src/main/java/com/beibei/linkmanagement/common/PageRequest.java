package com.beibei.linkmanagement.common;

import lombok.Data;

@Data
public class PageRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String linkType;
    private String vpsProvider;
    private String purchaser;
} 