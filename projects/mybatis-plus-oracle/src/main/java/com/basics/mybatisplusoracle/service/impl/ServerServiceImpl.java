package com.basics.mybatisplusoracle.service.impl;

import com.basics.mybatisplusoracle.entity.Server;
import com.basics.mybatisplusoracle.mapper.ServerMapper;
import com.basics.mybatisplusoracle.service.IServerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangbiyu
 * @since 2019-09-25
 */
@Service
public class ServerServiceImpl extends ServiceImpl<ServerMapper, Server> implements IServerService {

}
