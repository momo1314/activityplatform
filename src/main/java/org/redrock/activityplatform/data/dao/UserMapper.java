package org.redrock.activityplatform.data.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.redrock.activityplatform.data.domain.Message;
import org.redrock.activityplatform.data.domain.User;
import org.redrock.activityplatform.data.domain.UserMessage;

/**
 * Created by momo on 2018/5/3
 */
@Mapper
public interface UserMapper {
//    向user表添加信息
    boolean add(User user);
//    通过openid查询user
    User findByOpenid(@Param("openid") String openid );
//    通过sid查询user
    User findBySid(@Param("stuid") String stuid );

    User findBySidtoprize(@Param("stuid") String stuid );
//    更新user
    boolean update(User user);
//    清空表 上线禁用
    boolean delete();
}
