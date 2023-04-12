package com.app.ws.service.impl;

import com.app.ws.UserRepository;
import com.app.ws.io.entity.UserEntity;
import com.app.ws.service.UserService;
import com.app.ws.shared.Utils;
import com.app.ws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if(userEntity == null)
            throw new UsernameNotFoundException(username);

        return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),new ArrayList<>());
    }
}
