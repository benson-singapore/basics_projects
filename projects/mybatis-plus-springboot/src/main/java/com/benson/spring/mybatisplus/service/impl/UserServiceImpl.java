package com.benson.spring.mybatisplus.service.impl;

import com.benson.spring.mybatisplus.entity.User;
import com.benson.spring.mybatisplus.mapper.UserMapper;
import com.benson.spring.mybatisplus.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangbiyu
 * @since 2019-09-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
