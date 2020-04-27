package com.mengma.service;

import com.mengma.annotation.RPCService;
import org.springframework.stereotype.Service;

@Service
@RPCService(value = UserService.class)
public class UserServiceImpl implements UserService {

    public String sayHello(String word) {
        System.out.println("调用成功--参数 "+word);
        return "调用成功--参数 "+word;
    }

}
