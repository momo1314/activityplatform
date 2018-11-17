package org.redrock.activityplatform.data.domain;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
public class User {
    private Integer id;
    private String stuname;
    private Integer stuid;
    private String phonenum;
    private String openid;
    private String college;
    private String gender;
    private int status;
}
