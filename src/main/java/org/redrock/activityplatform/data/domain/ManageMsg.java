package org.redrock.activityplatform.data.domain;

import lombok.Data;

/**
 * Created by momo on 2018/9/15
 */
@Data
public class ManageMsg {
    private Integer cid;
    private String oname;
    private String dname;
    private String stuname;
    private String stuid;
    private String phonenum;
    private String info;
    private String result;
    private Integer status;
    private Integer currIndex;
    private Integer size;
    private String college;
    private String gender;
}
