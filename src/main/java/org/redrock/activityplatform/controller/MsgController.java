package org.redrock.activityplatform.controller;

import org.redrock.activityplatform.core.annotation.NeedJwt;
import org.redrock.activityplatform.data.domain.Message;
import org.redrock.activityplatform.data.service.MsgService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by momo on 2018/8/14
 */
@RestController
public class MsgController {
    @Resource
    private MsgService msgService;

    /**
     *  获取用户报名信息
     * @param openid
     * @return
     */
    @NeedJwt
    @PostMapping("/msg/cinfo")
    public ResponseEntity<List<Message>> cinfo(@RequestParam String openid) {
        List<Message> res = msgService.findByOpenid(openid);
        for (Message list:res) {
            list.setInfo(msgService.findInfo(list.getId()));
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * 刷新用户查看消息的状态
     * @param cid
     * @return
     */
    @NeedJwt
    @PostMapping("/msg/usersee")
    public ResponseEntity<String> usersee(@RequestParam Integer cid , @RequestParam String openid) {
        if(msgService.updateSee(cid , 1)){
            return new ResponseEntity<>("success", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("fail", HttpStatus.OK);
        }
    }



    /**
     * 用户报名 交付给AMQ-choose.queue
     * @param openid
     * @param oname
     * @param dname
     * @return
     */
    @NeedJwt
    @PostMapping("/msg/choose")
    public ResponseEntity<Integer> choose(@RequestParam String openid ,@RequestParam String oname , @RequestParam String dname){
        try {
            msgService.sendMsg("choose.queue", openid + "/" + oname + "/" + dname);
            return new ResponseEntity<>(200, HttpStatus.OK);
        }catch (Exception e){
            throw  new RuntimeException("消息队列发生错误");
        }
    }

    /**
     * 用户更新报名信息 交付给AMQ-rechoose.queue
     * @param openid
     * @param oldoname
     * @param olddname
     * @param newoname
     * @param newdname
     * @return
     */
    @NeedJwt
    @PostMapping("/msg/rechoose")
    public ResponseEntity<Integer> rechoose(@RequestParam String openid ,@RequestParam String oldoname, @RequestParam String olddname , @RequestParam String newoname , @RequestParam String newdname){
        try {
            msgService.sendMsg("rechoose.queue", openid + "/" + oldoname + "/" + olddname + "/" + newoname+ "/" + newdname);
            return new ResponseEntity<>(200, HttpStatus.OK);
        }catch (Exception e){
            throw  new RuntimeException("消息队列发生错误");
        }
    }

    /**
     * 删除报名信息 交付给AMQ-rechoose.queue
     * @param openid
     * @param oname
     * @param dname
     * @return
     */
    @NeedJwt
    @PostMapping("/msg/dechoose")
    public ResponseEntity<Integer> dechoose(@RequestParam String openid ,@RequestParam String oname, @RequestParam String dname){
        try {
            msgService.sendMsg("dechoose.queue", openid + "/" + oname + "/" + dname );
            return new ResponseEntity<>(200, HttpStatus.OK);
        }catch (Exception e){
            throw  new RuntimeException("消息队列发生错误");
        }
    }
}
