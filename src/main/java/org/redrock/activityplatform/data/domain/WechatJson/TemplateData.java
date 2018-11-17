package org.redrock.activityplatform.data.domain.WechatJson;

import lombok.Data;

/**
 * Created by momo on 2018/11/4
 */
@Data
public class TemplateData {
    private String value;
    private String color;
    public TemplateData(String value,String color){
        this.value = value;
        this.color = color;
    }
}
