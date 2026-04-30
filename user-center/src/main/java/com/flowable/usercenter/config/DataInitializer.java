package com.flowable.usercenter.config;

import com.flowable.usercenter.entity.SysUser;
import com.flowable.usercenter.mapper.SysUserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper userMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DataInitializer(SysUserMapper userMapper) { this.userMapper = userMapper; }

    @Override
    public void run(String... args) {
        var users = userMapper.selectList(null);
        for (var user : users) {
            if (user.getPassword() == null || user.getPassword().isBlank()
                    || user.getPassword().length() < 60) {
                user.setPassword(encoder.encode("123456"));
                userMapper.updateById(user);
            }
        }
    }
}
