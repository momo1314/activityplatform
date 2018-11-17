package org.redrock.activityplatform.data.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.codec.digest.DigestUtils;
import org.redrock.activityplatform.core.util.Const;
import org.redrock.activityplatform.core.util.CurlUtil;
import org.redrock.activityplatform.core.util.WeChatUtil;
import org.redrock.activityplatform.data.domain.Information;
import org.redrock.activityplatform.data.domain.User;
import org.redrock.activityplatform.data.domain.UserMessage;
import org.redrock.activityplatform.data.domain.WechatJson.TemplateData;
import org.redrock.activityplatform.data.domain.WechatJson.TemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.redrock.activityplatform.core.util.WeChatUtil.creatJson;
import static org.redrock.activityplatform.core.util.WeChatUtil.packJsonmsg;

/**
 * Created by momo on 2018/9/13
 */
@Service
public class WeChatService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InfoService infoService;

    @Autowired
    private MsgService msgService;




    public boolean WeChatPost(UserMessage user, Map data , String token , Map miniprogram , List<String> info , String template_id , Integer cid ,  Information inf){

        JSONObject jsonObject = WeChatUtil.creatJson(user ,info  , template_id , miniprogram , "" , "#000000");
        String errmsg = WeChatUtil.WeChatPost(user,data,jsonObject,token);
        if(!"ok".equals(errmsg)){  //如果为errmsg为ok，则代表发送成功，公众号推送信息给用户了。
            if(cid != -1){//处理直接推送的情况
                infoService.updateCStatusf(cid);
            }
            if(errmsg.equals("-1")){
                logger.error("Message processing error!");
            }else{
                logger.error("Template message post failed!");
            }
            return false;
        }
        infoService.add(inf);
            infoService.updateStatus(inf.getId());
        msgService.updateSee(cid,0);
        //todo:不需要更新状态了
        infoService.updateCStatus(cid);
        return true;
    }

//    public static void main(String[] args) {
//        long time = System.currentTimeMillis();
//        String str = "qingchunyouyue";
//        String secret = DigestUtils.shaHex(DigestUtils.shaHex(String.valueOf(time)) + DigestUtils.md5Hex(str) + "redrock");
//        Map<String, Object> data = new HashMap<>();
//        data.put("string", str);
//        data.put("timestamp", time);
//        data.put("secret", secret);
//        data.put("stuId", "2018213009");
//        JSONObject json = JSONObject.parseObject(CurlUtil.getContent(Const.Openid_URL , data , "POST"));
//        System.out.println(json.getString("openid").split("\\W")[0]);
//    }

}
