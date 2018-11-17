package org.redrock.activityplatform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.redrock.activityplatform.core.annotation.NeedJwt;
import org.redrock.activityplatform.core.util.AccountValidatorUtil;
import org.redrock.activityplatform.core.util.Const;
import org.redrock.activityplatform.core.util.CurlUtil;
import org.redrock.activityplatform.core.util.JwtHelper;
import org.redrock.activityplatform.data.domain.User;
import org.redrock.activityplatform.data.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by momo on 2018/5/3
 */
@RestController
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 检查是否存在用户/获取用户信息
     * @param openid
     * @param rsp
     * @return
     */
    @PostMapping("/user/findbyopenid")
    public ResponseEntity<User> findByOpenid(@RequestParam String openid , HttpServletResponse rsp){
        User user = userService.findByOpenid(openid);//20180001 - 20185202
        if(user == null){
            user = new User();
            user.setStatus(400);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }else {
//            user.setId(null);
//            user.setOpenid(null);
            user.setStatus(200);
            String jwt = JwtHelper.createJwtToken(user.getOpenid());
            rsp.setHeader("Authorization",jwt);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }


//    /**
//     *  未绑定小帮手用户 ---添加数据到数据库
//     * @param openid
//     * @param stuname
//     * @param stuid
//     * @param rsp
//     * @param phonenum
//     * @return
//     */
//    @PostMapping("/user/adduser")
//    public ResponseEntity<User> AddUser(@RequestParam String openid ,@RequestParam String stuname , @RequestParam Integer stuid , HttpServletResponse rsp,@RequestParam(required=false) String phonenum){
//        User user = userService.findByOpenid(openid);
//        if(user == null){
//            user = new User();
////            if(stuid>=20180001 && stuid<=20185202){
//                user.setStuid(stuid);
////            }else{
////                user.setStatus(400);
////                return new ResponseEntity<>(user,HttpStatus.OK);
////            }
//            if(openid.equals("")){
//                //不可以没有openid
//                user.setStatus(500);
//                return new ResponseEntity<>(user,HttpStatus.OK);
//            }
//            user.setOpenid(openid);
//            user.setStuname(stuname);
//            if(!phonenum.equals("")) {
////                if(AccountValidatorUtil.isMobile(phonenum)) {
//                    user.setPhonenum(phonenum);
////                }else{
////                    user.setStatus(400);
////                    return new ResponseEntity<>(user,HttpStatus.OK);
////                }
//            }
//            if(userService.add(user)){
//                String jwt = JwtHelper.createJwtToken(user.getOpenid());
//                rsp.setHeader("Authorization",jwt);
//                user.setStatus(200);
//                return new ResponseEntity<>(user,HttpStatus.OK);
//            }else{
//                user.setStatus(400);
//                return new ResponseEntity<>(user,HttpStatus.OK);
//            }
//        }else{
//            String jwt = JwtHelper.createJwtToken(user.getOpenid());
//            rsp.setHeader("Authorization",jwt);
//            user.setStatus(300);
//            return new ResponseEntity<>(user,HttpStatus.OK);
//        }
//    }

    /**
     *  修改个人信息
     * @param openid
     * @param idnum
     * @param stuid
     * @param phonenum
     * @return
     */
    @NeedJwt
    @PostMapping("/user/updateuser")
    public ResponseEntity<JSONObject> UpdateUser(@RequestParam String openid ,@RequestParam String idnum , @RequestParam Integer stuid , @RequestParam(required=false) String phonenum){
        User user = new User();
        JSONObject jsonObject = new JSONObject();
//        if(stuid>=20180001 && stuid<=20185202){
            user.setStuid(stuid);
//        }else{
//              jsonObject.put("errmsg","invalid parameter");
//              return new ResponseEntity<>(jsonObject,HttpStatus.valueOf(400));
//        }
        user.setOpenid(openid);
        Map<String , Object> map = new HashMap<>();
        map.put("stuNum",stuid);
        map.put("idNum",idnum);
        JSONObject json = JSONObject.parseObject(CurlUtil.getContent("https://wx.idsbllp.cn/api/verify", map, "POST"));
        if(json.getInteger("status") == 200){
            JSONObject json1 = JSONObject.parseObject(json.get("data").toString());
            String stuname = json1.getString("name");
            String college = json1.getString("college");
            String gender = json1.getString("gender");
            user.setStuname(stuname);
            user.setCollege(college);
            user.setGender(gender);
            if(userService.update(user)){
                jsonObject.put("errmsg","success");
                return new ResponseEntity<>(jsonObject,HttpStatus.OK);
            }else{
                jsonObject.put("errmsg","sql error");
                return new ResponseEntity<>(jsonObject,HttpStatus.valueOf(501));
            }
        }else if(json.getInteger("status") == 201 ){
            jsonObject.put("errmsg","authentication error");
            return new ResponseEntity<>(jsonObject,HttpStatus.valueOf(500));
        }else if(json.getInteger("status") == -100){
            jsonObject.put("errmsg","student id error");
            return new ResponseEntity<>(jsonObject,HttpStatus.valueOf(400));
        }else if(json.getInteger("status") == 801){
            jsonObject.put("errmsg","invalid parameter");
            return new ResponseEntity<>(jsonObject,HttpStatus.valueOf(400));
        }else {
            jsonObject.put("errmsg","authentication error");
            return new ResponseEntity<>(jsonObject,HttpStatus.valueOf(501));
        }
    }

    /**
     * 未绑定小帮手用户 ---添加数据到数据库
     * @param openid
     * @param stuid
     * @param idnum
     * @param rsp
     * @param phonenum
     * @return
     */
    @PostMapping("/user/adduser")
    public ResponseEntity<User> AddUsert(@RequestParam String openid  , @RequestParam Integer stuid, @RequestParam String idnum , HttpServletResponse rsp,@RequestParam(required=false) String phonenum){
        User user = userService.findByOpenid(openid);
        if(user == null){
            user = new User();
//            if(stuid>=20180001 && stuid<=20185202){
            user.setStuid(stuid);
//            }else{
//                user.setStatus(400);
//                return new ResponseEntity<>(user,HttpStatus.valueOf(400));
//            }
            if(openid.equals("") | openid.equals("[Object object]")){
                //不可以没有openid(处理前端获取openid失败的情况
                user.setStatus(400);
                return new ResponseEntity<>(user,HttpStatus.valueOf(400));
            }
            user.setOpenid(openid);
            if(!phonenum.equals("")) {
//                if(AccountValidatorUtil.isMobile(phonenum)) {
                user.setPhonenum(phonenum);
//                }else{
//                    user.setStatus(400);
//                    return new ResponseEntity<>(user,HttpStatus.valueOf(400));
//                }
            }
            Map<String , Object> map = new HashMap<>();
            map.put("stuNum",stuid);
            map.put("idNum",idnum);
            JSONObject json = JSONObject.parseObject(CurlUtil.getContent("https://wx.idsbllp.cn/api/verify", map, "POST"));
            if(json.getInteger("status") == 200){
                JSONObject json1 = JSONObject.parseObject(json.get("data").toString());
                String stuname = json1.getString("name");
                String college = json1.getString("college");
                String gender = json1.getString("gender");
                user.setCollege(college);
                user.setGender(gender);
                user.setStuname(stuname);
                if(userService.add(user)){
                    String jwt = JwtHelper.createJwtToken(user.getOpenid());
                    rsp.setHeader("Authorization",jwt);
                    user.setStatus(200);
                    return new ResponseEntity<>(user,HttpStatus.OK);
                }else{
                    user.setStatus(500);
                    return new ResponseEntity<>(user,HttpStatus.valueOf(501));
                }
            }else if(json.getInteger("status") == 201 ){
                user.setStatus(500);
                user.setStuname("authentication error");
                return new ResponseEntity<>(user,HttpStatus.valueOf(500));
            }else if(json.getInteger("status") == -100){
                user.setStatus(400);
                user.setStuname("student id error");
                return new ResponseEntity<>(user,HttpStatus.valueOf(400));
            }else if(json.getInteger("status") == 801){
                user.setStatus(400);
                user.setStuname("invalid parameter");
                return new ResponseEntity<>(user,HttpStatus.valueOf(400));
            }else {
                user.setStatus(500);
                user.setStuname("authentication error");
                return new ResponseEntity<>(user,HttpStatus.valueOf(501));
            }
        }else{
            String jwt = JwtHelper.createJwtToken(user.getOpenid());
            rsp.setHeader("Authorization",jwt);
            user.setStatus(300);//已存在数据
            return new ResponseEntity<>(user,HttpStatus.OK);
        }
    }

    /**
     * 后端给前端返回用户openid
     * @param code
     * @return
     */
    @PostMapping("/user/getopenid")
    public ResponseEntity<String> UserInfo(@RequestParam String code){
        return new ResponseEntity<>(userService.getOpenid(code),HttpStatus.OK);
    }

//    public static void main(String[] args) {
//        Map<String , Object> map = new HashMap<>();
//        map.put("stuNum","2015211878");
//        map.put("idNum","024914");
//        JSONObject json = JSONObject.parseObject(CurlUtil.getContent("https://wx.idsbllp.cn/api/verify", map, "POST"));
//        if(json.getInteger("status") == 200){
//            JSONObject json1 = JSONObject.parseObject(json.get("data").toString());
//            System.out.println(json1.toString());
//        }else if(json.getInteger("status") == 201 ){
//            System.out.println(json.toString());
//        }else if(json.getInteger("status") == -100){
//            System.out.println(json.toString());
//        }else if(json.getInteger("status") == 801){
//            System.out.println(json.toString());
//        }else {
//            System.out.println(json.toString());
//        }
//    }

}
