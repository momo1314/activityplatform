package org.redrock.activityplatform.data.domain;

import lombok.Data;

import java.util.List;

@Data
public class Message {
    private Integer id;
    private String openid;
    private String oname;
    private String dname;
    private int see;
    private List<Information> info;
}
