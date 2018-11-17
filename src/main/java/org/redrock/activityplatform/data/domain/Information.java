package org.redrock.activityplatform.data.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by momo on 2018/8/18
 */
@Data
public class Information {
    private Integer id;
    private Integer cid;
    private String info;
    private String time;
    private int status;
}
