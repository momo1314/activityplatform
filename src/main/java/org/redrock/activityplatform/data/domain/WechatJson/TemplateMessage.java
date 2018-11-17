package org.redrock.activityplatform.data.domain.WechatJson;

import lombok.Data;

import java.util.Map;

/**
 * Created by momo on 2018/11/4
 */
@Data
public class TemplateMessage {
    private String touser; //用户OpenID
    private String template_id; //模板消息ID
    private String url; //URL置空，在发送后，点模板消息进入一个空白页面（ios），或无法点击（android）。
    private String topcolor; //标题颜色
    private Map<String, TemplateData> templateData; //模板详细信息
}
