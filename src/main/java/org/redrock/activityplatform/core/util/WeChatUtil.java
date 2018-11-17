package org.redrock.activityplatform.core.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.redrock.activityplatform.data.domain.Information;
import org.redrock.activityplatform.data.domain.UserMessage;
import org.redrock.activityplatform.data.domain.WechatJson.TemplateData;
import org.redrock.activityplatform.data.domain.WechatJson.TemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by momo on 2018/10/1
 */
public class WeChatUtil {
    private static Logger logger = LoggerFactory.getLogger(WeChatUtil.class);
    private static final String tmpurl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
    /**
     * 推送微信消息
     * @param user
     * @param data
     * @param jsonObject
     * @param token
     * @return
     */
    public static String WeChatPost(UserMessage user, Map data , JSONObject jsonObject , String token) {
        String url = tmpurl.replace("ACCESS_TOKEN", token);
        try {
            JSONObject json = JSONObject.parseObject(CurlUtil.getContent(Const.Openid_URL, data, "POST"));
            user.setOpenid(json.getString("openid").split("\\W")[0]);//获取接口的openid
            String result = CurlUtil.postData(url, jsonObject.toJSONString(jsonObject, SerializerFeature.DisableCircularReferenceDetect));
            JSONObject resultJson = JSONObject.parseObject(result);
            logger.info("学号" + user.getStuid() + ",微信返回结果:" + resultJson.toJSONString());
            String errmsg = (String) resultJson.get("errmsg");
            return errmsg;
        }catch (Exception e) {
            logger.error("返回结果解析失败或对方未绑定,请后端检查参数:"+e);
            return "-1";
        }
    }

    /**
     * 封装模板详细信息
     * @param param
     * @return
     */
    public static JSONObject packJsonmsg(Map<String, TemplateData> param) {
        JSONObject json = new JSONObject();
        for (Map.Entry<String,TemplateData> entry : param.entrySet()) {
            JSONObject keyJson = new JSONObject();
            TemplateData  dta=  entry.getValue();
            keyJson.put("value",dta.getValue());
            keyJson.put("color", dta.getColor());
            json.put(entry.getKey(), keyJson);
        }
        return json;
    }

    /**
     * 用于创建推送的消息体
     * @param user
     * @param info
     * @param template_id
     * @param miniprogram
     * @return
     */
    public static JSONObject creatJson(UserMessage user, List<String> info, String template_id  , Map miniprogram , String clickurl , String topcolor ){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser" , user.getOpenid());
        jsonObject.put("template_id" , template_id);
        jsonObject.put("url", clickurl);
        jsonObject.put("topcolor", topcolor);
//        jsonObject.put("miniprogram",miniprogram); ---设置跳转
        Map<String,TemplateData> param = new HashMap<>();

        //TODO:更新模板的时候处理
        if(template_id.equals("H3VNgVqo3r9ewRi0hhGJDKl_-VBginnIgtFmNyRXeiM")){
//            {{first.DATA}}
//            社团组织：{{keyword1.DATA}}
//            面试地点：{{keyword2.DATA}}
//            面试时间：{{keyword3.DATA}}
//            联系人姓名：{{keyword4.DATA}}
//            联系人联系方式：{{keyword5.DATA}}
//            {{remark.DATA}}
            // info 0 name  1 oname+dname 2 addr 3 time 4 sname 5 sphone 6 last
            param.put("first",new TemplateData("亲爱的"+ user.getStuname() +"同学,你先前的申请的社团需要"+info.get(0)+"，现将详细面试信息发给你。","#696969"));
            param.put("keyword1",new TemplateData(user.getOname().equals(user.getDname())?user.getOname():user.getOname()+user.getDname(),"#696969"));
            param.put("keyword2",new TemplateData(info.get(1),"#696969"));
            param.put("keyword3",new TemplateData(info.get(2),"#696969"));
            param.put("keyword4",new TemplateData(info.get(3),"#696969"));
            param.put("keyword5",new TemplateData(info.get(4),"#696969"));
            param.put("remark",new TemplateData("","#696969"));
        }else if(template_id.equals("ptsau_vXeAlzsRubHLqaxlFkyicvDbMUJLGCXdniK_g")) {
//            //info 0 oname 1  dname 2.res 3.last
            param.put("first",new TemplateData(user.getOname(),"#696969"));
            param.put("keyword1",new TemplateData(user.getStuname(),"#696969"));
            param.put("keyword2",new TemplateData(user.getDname(),"#696969"));
            param.put("keyword3",new TemplateData(info.get(0),"#696969"));
            param.put("remark",new TemplateData("","#696969"));
        }else if(template_id.equals("yOVleiFdO7IzGRQS1v-CVxA_QQGYSGS8iZXXfRuxkz0")) {
//            //info 0 first 1  活动名 2.奖品 3.时间 4.地点(请尽快到红岩网校工作站B区（太极西六门上三楼左转）领取！)
            param.put("first",new TemplateData(user.getStuname()+"同学,恭喜你获奖,这里有份礼物等你查收~","#696969"));
            param.put("keyword1",new TemplateData(info.get(0),"#696969"));
            param.put("keyword2",new TemplateData(info.get(1),"#696969"));
            param.put("keyword3",new TemplateData(info.get(2),"#696969"));
            param.put("remark",new TemplateData("请尽快到红岩网校工作站B区（太极西六门上三楼左转）领取！","#696969"));
        }
        jsonObject.put("data",packJsonmsg(param));
        return jsonObject;
    }
}
