package org.redrock.activityplatform.data.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.redrock.activityplatform.data.domain.*;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

/**
 * Created by momo on 2018/9/1
 */
@Mapper
public interface InfoMapper {

//    添加info表信息->推送消息表
    Integer add(Information info);
//    通过cid寻找info消息
    List<Information> findInfo(@Param("cid") Integer cid);
//    删除推送信息
    boolean delete(Map map);

    boolean deletebyid(@Param("id") Integer id);
//    获取总报名数
    Integer allcount(@Param("oname") String oname);
//    获取总用户数
    Integer usercount(@Param("oname") String oname);
//    根据输入变量不同 获取不同组织和状态下的总人数
    Integer ocount(@Param("oname") String oname , @Param("dname") String dname , @Param("info") String info , @Param("result") String result);

//    获取管理员名字
    Manage finduser(@Param("username") String username);

//    通过cid获取用户信息
    UserMessage finduserinfobycid(@Param("cid") Integer cid);

    boolean updatefee(Map map);

    List<ManageMsg> findchoose(ManageMsg mmsg);

    Integer getsize(ManageMsg mmsg);

    boolean updateC(ManageMsg mmsg);

    boolean updateCbyid(Map map );

    boolean updateC0(@Param("id") Integer id);

    boolean update(@Param("id") Integer id);

    boolean updateCStatus(@Param("id") Integer id);

    boolean updateCStatusf(@Param("id") Integer id);

    List<String> getcinfolist(@Param("dname") String dname , @Param("oname") String oname);

    List<String> getcreslist();

    Map getfee(@Param("oname") String oname);
}
