package org.redrock.activityplatform.data.service;

import org.apache.activemq.command.ActiveMQQueue;
import org.redrock.activityplatform.data.dao.MsgMapper;
import org.redrock.activityplatform.data.domain.Information;
import org.redrock.activityplatform.data.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;
import java.util.List;

/**
 * Created by momo on 2018/8/14
 */
@Service
public class MsgService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MsgMapper msgMapper;

    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 向对应的队列发送信息
     * @param destinationName
     * @param message
     */
    public void sendMsg(String destinationName , String message){
        Destination destination = new ActiveMQQueue(destinationName);
        jmsMessagingTemplate.convertAndSend(destination,message);
    }

    public boolean deletebyid(Integer id){
        return msgMapper.deletebyid(id);
    }
    public  boolean deleteInfo(Integer id){
        return msgMapper.deleteinfo(id);
    }

    /**
     * 通过openid查询choose表
     * @param openid
     * @return
     */
    public List<Message> findByOpenid(String openid){
        return msgMapper.findByOpenid(openid);
    }

    /**
     * 向choose表插入用户信息
     * @param openid
     * @param oname
     * @param dname
     * @return
     */
    public int add(String openid, String oname , String dname){
        return msgMapper.addC(openid,oname,dname);
    }

    /**
     * 在choose表更新相应用户信息
     * @param openid
     * @param oldoname
     * @param olddname
     * @param newoname
     * @param newdname
     * @return
     */
    public boolean update(String openid , String oldoname , String olddname  ,String newoname , String newdname){
        return msgMapper.updateC(openid , oldoname ,olddname  , newoname ,newdname);
    }

    /**
     * 处理小程序中用户是否看过的存储
     * @param cid
     * @param see
     * @return
     */
    public boolean updateSee(Integer cid , Integer see) {
        return msgMapper.updateSee(cid , see);
    }

    /**
     * 通过choose表的id查询info表的list
     * @param cid
     * @return
     */
    public List<Information> findInfo(Integer cid) {
        return msgMapper.findinfo(cid);
    }

}
