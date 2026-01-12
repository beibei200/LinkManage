package com.beibei.linkmanagement.service.impl;

import com.beibei.linkmanagement.entity.OperationLog;
import com.beibei.linkmanagement.mapper.OperationLogMapper;
import com.beibei.linkmanagement.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperationLogServiceImpl implements OperationLogService {
    
    private final OperationLogMapper operationLogMapper;
    
    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }
    
    @Override
    @Transactional
    public void logOperation(Long userId, String operationType, Long linkId, String description) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setOperationType(operationType);
        log.setLinkId(linkId);
        log.setDescription(description);
        operationLogMapper.insert(log);
    }
} 