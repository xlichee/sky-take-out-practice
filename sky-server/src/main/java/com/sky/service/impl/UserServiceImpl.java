package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;



    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {



        String openid = getOpenid(userLoginDTO.getCode());

        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

        }

        User user = userMapper.getByOpenid(openid);

        if(user==null){
            // 创建新用户，保存用户信息
            user = User.builder()
                .openid(openid)
                .name(userLoginDTO.getName())
                .avatar(userLoginDTO.getAvatar())
                .sex(String.valueOf(userLoginDTO.getSex()))
                .createTime(LocalDateTime.now())
                .build();
            userMapper.insert(user);
        } else if (userLoginDTO.getName() != null && userLoginDTO.getAvatar() != null) {
            // 更新已有用户的信息
            user.setName(userLoginDTO.getName());
            user.setAvatar(userLoginDTO.getAvatar());
            user.setSex(String.valueOf(userLoginDTO.getSex()));
            userMapper.update(user);
        }


        return user;
    }

    private  String getOpenid(String code){

        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        return openid;
    }
}
