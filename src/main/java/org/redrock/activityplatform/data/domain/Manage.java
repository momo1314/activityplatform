package org.redrock.activityplatform.data.domain;

import lombok.Data;

/**
 * Created by momo on 2018/9/12
 */
@Data
public class Manage {
    private String name;
    private String password;
    private Integer limit;
    private Integer fee;
}
