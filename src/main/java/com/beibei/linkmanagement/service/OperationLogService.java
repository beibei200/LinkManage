package com.beibei.linkmanagement.service;

import com.beibei.linkmanagement.entity.OperationLog;

public interface OperationLogService {
    void logOperation(Long userId, String operationType, Long linkId, String description);
} 