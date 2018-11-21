package org.redrock.activityplatform.data.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.redrock.activityplatform.data.dao.InfoMapper;
import org.redrock.activityplatform.data.dao.MsgMapper;
import org.redrock.activityplatform.data.dao.UserMapper;
import org.redrock.activityplatform.data.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by momo on 2018/9/1
 */
@Service
public class InfoService {

    @Autowired
    private InfoMapper infoMapper;


//    public boolean adduser(String oname , String dname , String stuname , String stuid ,String phonenum ,String info) {
//        // User user = new User();
//        // user.setOpenid(stuid);
//        // user.setStuname(stuname);
//        // user.setStuid(stuid);
//        // user.setPhonenum(phonenum);
//        // if(userMapper.add(user)){
//        //     int cid = msgMapper.addC(stuid,oname,dname);
//        //     if(cid > 0) {
//        //         userMapper.delete(stuid);
//        //         return false;
//        //     }
//        //     Information im = new Information();
//        //     im.setCid(cid);
//        //     im.setInfo(info);
//        //     if(infoMapper.add(im)){
//        //         return true;
//        //     }else{
//        //         msgMapper.deleteinfo(cid);
//        //         userMapper.delete(stuid);
//        //         return false;
//        //     }
//        // }else{
//        //     return false;
//        // }
//        return false;
//    }

    public Integer add(Information info) {
        return  infoMapper.add(info);
    }

    public List<Information> find(Integer cid) {
        return  infoMapper.findInfo(cid);
    }

    public boolean delete(Integer cid , String time) {
        Map<String , Object> map = new HashMap<>();
        map.put("cid" , cid);
        map.put("time" , time);
        return infoMapper.delete(map);
    }

    public boolean deletebyid(Integer id) {
        return infoMapper.deletebyid(id);
    }

    /**
     * 登录验证
     * @param manage
     * @return
     */
    public Manage login(Manage manage){
        Manage user = infoMapper.finduser(manage.getName());
        if(user != null) {
            if (user.getPassword().equals(DigestUtils.md5Hex(manage.getPassword()))) {
                return user;
            } else {
                return null;
            }
        }else{
            return null;
        }
    }

    public Manage finduser(String oname){
        return infoMapper.finduser(oname);
    }

    /**
     * 保存短信计费
     * @param fee
     * @param oname
     * @return
     */
    public boolean updatefee(Integer fee , String oname){
        Map<String , Object> map = new HashMap<>();
        map.put("fee" , fee);
        map.put("oname" , oname);
        return infoMapper.updatefee(map);
    }

    /**
     * 获取该组织的user计数(总用户数)
     * @param oname
     * @return
     */
    public Integer usercount(String oname){
        return  infoMapper.usercount(oname);
    };

    /**
     * 获取不同组织和状态下的总 人数
     * @param oname
     * @param dname
     * @param info
     * @param result
     * @return
     */
    public Integer ocount(String oname ,String dname , String info , String result) {
        return infoMapper.ocount(oname,dname , info , result);
    };

    /**
     * 获取组织总报名数
     * @param oname
     * @return
     */
    public  Integer  allcount(String oname){
        return infoMapper.allcount(oname);
    }

    /**
     * 多条件查找搜索用户
     * @param mmsg
     * @return
     */
    public List<ManageMsg> findchoose(ManageMsg mmsg){
        mmsg.setCurrIndex((int)((mmsg.getCurrIndex()-1)*mmsg.getSize()));
        return infoMapper.findchoose(mmsg);
    }

    /**
     * 多条件状态和size下的页数
     * @param mmsg
     * @return
     */
    public Integer getsize(ManageMsg mmsg){
        //获取总页数
        return infoMapper.getsize(mmsg);
    }

    public boolean updateC(ManageMsg mmsg){
        return infoMapper.updateC(mmsg);
    }

    /**
     * 下面4个为存储当次推送的状态，下次推送的时候覆盖
     * @param id
     * @return
     */
    public boolean updateStatus(Integer id){
        return infoMapper.update(id);
    }
    public boolean updateC0(Integer id){
        return infoMapper.updateC0(id);
    }
    public boolean updateCStatus(Integer id){
        return infoMapper.updateCStatus(id);
    }
    public boolean updateCStatusf(Integer id){
        return infoMapper.updateCStatusf(id);
    }

    /**
     * 更新用户的后台备注与状态
     * @param id
     * @param info
     * @param result
     * @return
     */
    public  boolean updateCbyid(Integer id , String info , String result ){
        Map<String , Object> map = new HashMap<>();
        map.put("id" , id);
        map.put("info" , info);
        map.put("result", result);
        return infoMapper.updateCbyid(map);
    }


    /**
     * 通过choose表的id获取整体用户信息
     * @param cid
     * @return
     */
    public UserMessage finduserinfobycid(Integer cid){
        return infoMapper.finduserinfobycid(cid);
    }

    /**
     * 获得choose表的相应信息
     * @param oname
     * @param dname
     * @return
     */
    public List<String> getcinfolist(String oname , String dname){
        return infoMapper.getcinfolist(dname, oname);
    }

    public List<String> getcreslist(){
        return infoMapper.getcreslist();
    }

    /**
     * 获取短信计费
     * @param name
     * @return
     */
    public Map getfee(String name){
        return infoMapper.getfee(name);
    }
}
