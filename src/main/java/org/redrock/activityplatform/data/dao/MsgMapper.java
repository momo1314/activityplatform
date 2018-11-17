package org.redrock.activityplatform.data.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.redrock.activityplatform.data.domain.Information;
import org.redrock.activityplatform.data.domain.Message;

import java.util.List;

/**
 * Created by momo on 2018/8/14
 */
@Mapper
public interface MsgMapper {
//    向choose 用户选择与组织信息填充表 加入消息
    int addC(@Param("openid") String openid , @Param("oname") String oname ,@Param("dname") String dname);
//    通过openid查询choose表
    List<Message> findByOpenid(@Param("openid") String openid);
//    准确查找个人
    Message findOne(@Param("openid") String openid , @Param("oname") String oname ,@Param("dname") String dname);
//    更新单个
    boolean updateC(@Param("openid") String openid ,@Param("oldoname") String oldoname ,@Param("olddname") String olddname  ,@Param("newoname") String newoname ,@Param("newdname") String newdname);
//   通过详细信息 删除单个
    boolean deleteC(@Param("openid") String openid , @Param("oname") String oname ,@Param("dname") String dname);
//    通过id删除单个
    boolean deletebyid(@Param("id") Integer id);
//    更新用户查看推送的情况
    boolean updateSee(@Param("cid") Integer cid ,@Param("see") Integer see );
//    通过choose表的id查询info表的list
    List<Information> findinfo(@Param("cid") Integer cid);
//    批量删除与该id相关的所有info
    boolean deleteinfo(@Param("cid") Integer cid);
}
