package org.redrock.activityplatform.data.service;

import org.redrock.activityplatform.core.util.Const;
import org.redrock.activityplatform.data.dao.UserMapper;
import org.redrock.activityplatform.data.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.redrock.activityplatform.core.util.CurlUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;

/**
 * Created by momo on 2018/5/3
 */
@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserMapper userMapper;
//    @Resource
//    private MsgMapper msgMapper;

    /**
     * 通过openid获取用户信息
     * @param openid
     * @return
     */
    public User findByOpenid(String openid){
        return userMapper.findByOpenid(openid);
    }

    public User findBySid(String stuid){
        return userMapper.findBySid(stuid);
    }

    public User findBySidtoprize(String stuid){
        return userMapper.findBySidtoprize(stuid);
    }

    /**
     * 添加用户信息
     * @param user
     * @return
     */
    public boolean add(User user){
        return userMapper.add(user);
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    public boolean update(User user){
        User ouser = userMapper.findByOpenid(user.getOpenid());
        if(ouser.getStuid() == user.getStuid()){
            return userMapper.update(user);
        }else{
            return false;
        }

    }

//    public UserMessage getMessage(String openid) {
//        User user = userMapper.findByOpenid(openid);
//        List<Message> message = msgMapper.findByUid(user.getId());
//        UserMessage userMessage = new UserMessage();
//        userMessage.setId(user.getId());
//        userMessage.setPhoneNum(user.getPhoneNum());
//        userMessage.setStuid(user.getStuid());
//        userMessage.setStuname(user.getStuname());
//        userMessage.setOrganize(message);
//        return userMessage;
//    }

    /**
     * 通过前端给的code获取用户openid
     * @param code
     * @return
     */
    public String getOpenid(String code){
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+ Const.APPID+"&secret="+Const.SECRET+"&js_code="+code+"&grant_type=authorization_code";
	    String res = CurlUtil.getContent(url, null, "GET");
	    JSONObject jsonObject = JSON.parseObject(res);
	    try{
            if(jsonObject.getString("openid").equals(null)){
                return "Getting openid failed!";
            }
            return jsonObject.getString("openid");
        }catch (Exception e){
	        throw new RuntimeException("Got Error form getOpenid :"+jsonObject.toJSONString());
        }


    }



}
