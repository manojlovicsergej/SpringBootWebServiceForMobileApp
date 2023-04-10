package com.app.ws.service.impl;

import com.app.ws.UserRepository;
import com.app.ws.io.entity.UserEntity;
import com.app.ws.service.UserService;
import com.app.ws.shared.Utils;
import com.app.ws.shared.dto.UserDto;
import jdk.jshell.execution.Util;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {

        if(userRepository.findByEmail(userDto.getEmail())!=null)
            throw new RuntimeException("Record already exists");

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDto,userEntity);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setUserId(utils.generateUserId(30));

        UserEntity storedUserDetails = userRepository.save(userEntity);
        UserDto returnValue = new UserDto();

        BeanUtils.copyProperties(storedUserDetails,returnValue);
        return returnValue;
    }
}
