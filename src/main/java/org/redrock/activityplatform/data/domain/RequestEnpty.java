package org.redrock.activityplatform.data.domain;

import lombok.Data;

import java.util.List;

/**
 * Created by momo on 2018/9/16
 */
@Data
public class RequestEnpty {
    private List<Integer> id;
    private String beizhu;
    private String tid;
    private List<String> info;
    private String result;
    private Integer choose;
}
