package org.redrock.activityplatform.data.service;

import org.redrock.activityplatform.data.dao.MsgMapper;
import org.redrock.activityplatform.data.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by momo on 2018/8/14
 */
@Service
public class MsgMqService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MsgMapper msgMapper;

    /**
     * 监听choose队列，处理用户报名
     * @param message
     */
    @JmsListener(destination = "choose.queue")
    public void choose(String message){
        String[] res = message.split("/");
        String openid = res[0];
        String oname = res[1];
        String dname = res[2];
        Message msg = msgMapper.findOne(openid,oname,dname);
        if(msg == null){
            msgMapper.addC(openid,oname,dname);
        }
    }
    /**
     * 监听rechoose队列，处理用户更改相应报名
     * @param message
     */
    @JmsListener(destination = "rechoose.queue")
    public void rechoose(String message){
        String[] res = message.split("/");
        String openid = res[0];
        String oldoname = res[1];
        String olddname = res[2];
        String newoname = res[3];
        String newdname = res[4];
        Message msg = msgMapper.findOne(openid,oldoname,olddname);
        Message newmsg = msgMapper.findOne(openid,newoname,newdname);
        msgMapper.deleteinfo(msg.getId());
        if(newmsg == null){
            msgMapper.updateC(openid , oldoname ,olddname  , newoname ,newdname );
        }else{
            msgMapper.deleteC(openid,oldoname,olddname);
        }
    }
    /**
     * 监听dechoose队列，处理用户取消报名
     * @param message
     */
    @JmsListener(destination = "dechoose.queue")
    public void dechoose(String message){
        String[] res = message.split("/");
        String openid = res[0];
        String oname = res[1];
        String dname = res[2];
        Message msg = msgMapper.findOne(openid,oname,dname);
        msgMapper.deleteinfo(msg.getId());
        msgMapper.deleteC(openid,oname,dname);
    }
}
