package org.redrock.activityplatform.data.service;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.redrock.activityplatform.data.domain.Information;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;

import javax.jws.Oneway;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by momo on 2018/9/14
 * 这是一个调用腾讯云短信api的接口
 */
@Service
public class SmsService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // 短信应用SDK AppID
    private static int appid = 1400129783; // 1400开头

    @Autowired
    private InfoService infoService;

    @Autowired
    private MsgService msgService;
    // 短信应用SDK AppKey
    private static String appkey = "0f3639ae931be7bc4ebba8e9d6c2d3bd";

    // 需要发送短信的手机号码

    // 短信模板ID，需要在短信应用中申请

    private static String smsSign = "青春邮约"; // NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`

    public boolean sendSms(String[] phoneNumbers , List<String> params  , Integer templateId , Integer cid ,  Information inf , String oname) {
//        List<String> phoneNumbers =new ArrayList<>();
//        phoneNumbers.add("13008332289");
//        List<String> params =new ArrayList<>();
//        params.add("你");
//        params.add("在网校扫地一周");
        try {
           //数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
//            SmsMultiSender msender = new SmsMultiSender(appid, appkey);
//            SmsMultiSenderResult result =  msender.sendWithParam("86", phoneNumbers,
//                    templateId, params.toArray(new String[params.size()]), smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
                    templateId, params.toArray(new String[params.size()]), (oname.equals("红岩网校工作站"))?"红岩网校工作站":smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            logger.info("SMS Return:"+result);
            if(result.errMsg.equals("OK")) {
                infoService.add(inf);
                msgService.updateSee(cid,0);
                infoService.updatefee(result.fee,oname);
                //todo:考虑删除
                infoService.updateStatus(inf.getId());
                infoService.updateCStatus(cid);

                return true;
            }else{
                infoService.updateCStatusf(cid);
                logger.info("errMsg not OK , Sent not success"+result.toString());
                return false;
            }
        } catch (HTTPException e) {
            infoService.updateCStatusf(cid);
            logger.info("HTTP  code error:"+e);
            return false;
         } catch (JSONException e) {
            infoService.updateCStatusf(cid);
            logger.info("Parsing JSON error:"+e);
            return false;
            //e.printStackTrace();
        } catch (IOException e) {
            infoService.updateCStatusf(cid);
            logger.info("IO error:"+e);
            return false;
            //e.printStackTrace();
        }
    }

}
