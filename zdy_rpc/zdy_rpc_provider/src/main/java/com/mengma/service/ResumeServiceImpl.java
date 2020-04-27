package com.mengma.service;

import com.mengma.annotation.RPCService;
import org.springframework.stereotype.Service;

/**
 * @author fgm
 * @description
 * @date 2020-04-25
 ***/
@Service
@RPCService(value = ResumeService.class)
public class ResumeServiceImpl implements ResumeService {
    @Override
    public String getResumeById(Long id) {
        return "萌马的简历："+id;
    }
}
