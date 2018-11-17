package org.redrock.activityplatform.data.domain;


import lombok.Data;

import java.util.List;

/**
 * Created by momo on 2018/8/14
 */
@Data
public class UserMessage {
    private String stuname;
    private Integer stuid;
    private String phonenum;
    private String oname;
    private String dname;
    private String openid;
    private String result;
}
