package org.redrock.activityplatform.controller;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import org.apache.commons.codec.digest.DigestUtils;
import org.redrock.activityplatform.core.annotation.BackJwt;
import org.redrock.activityplatform.core.util.Const;
import org.redrock.activityplatform.core.util.CurlUtil;
import org.redrock.activityplatform.core.util.JwtHelper;
import org.redrock.activityplatform.data.dao.MsgMapper;
import org.redrock.activityplatform.data.domain.*;
import org.redrock.activityplatform.data.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.redrock.activityplatform.core.interceptor.InitInterceptor.ACCESS_TOKEN;

/**
 * Created by momo on 2018/9/1
 */
@RestController
public class BackController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private InfoService infoService;


    @Autowired
    private UserService userService;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private SmsService smsService;

//    /**
//     * 后台单独添加用户接口
//     * @param oname
//     * @param dname
//     * @param stuname
//     * @param stuid
//     * @param phonenum
//     * @param info
//     * @return
//     */
//    //@PostMapping("/469bba0a564235dfceede42db14f17b0/test1")
//    public ResponseEntity<String> addUser(String oname , String dname , String stuname , String stuid ,String phonenum ,String info){
//        boolean res = infoService.adduser(oname, dname, stuname, stuid, phonenum, info);
//        return  new ResponseEntity<>(res == true ?"success":"fail" , HttpStatus.OK);
//    }

    /**
     * 给定用户登录
     * @param username
     * @param passwd
     * @param rsp
     * @return
     */
    @PostMapping("/469bba0a564235dfceede42db14f17b0/login")
    public ResponseEntity<Manage> login(@RequestParam String username, @RequestParam String passwd, HttpServletResponse rsp) {
        Manage manage = new Manage();
        manage.setName(username);
        manage.setPassword(passwd);
        manage = infoService.login(manage);
        if (manage != null) {
            String jwt = JwtHelper.createJwtToken(username);
            rsp.setHeader("Authorization", jwt);
            manage.setPassword(null);
            return new ResponseEntity<>(manage, HttpStatus.OK);
        }
        manage = new Manage();
        manage.setName("login fail!");
        return new ResponseEntity<>(manage, HttpStatus.valueOf(500));
    }
//
//    /**
//     * 后台单独添加info接口
//     * @param cid
//     * @param info
//     * @return
//     */
//    @BackJwt
//    @PostMapping("/469bba0a564235dfceede42db14f17b0/addinfo")
//    public ResponseEntity<String> addinfo(@RequestParam Integer cid , @RequestParam String info ){
//        Information inf  = new Information();
//        inf.setInfo(info);
//        inf.setCid(cid);
//        return  new ResponseEntity<>(infoService.add(inf) ?"success":"fail" , HttpStatus.OK);
//    }

    /**
     *  后台批量添加info
     * @param enpty
     * @return
     */
    @BackJwt
    @PostMapping("/469bba0a564235dfceede42db14f17b0/addinfolist")
    public ResponseEntity<PostResult> addinfolist(@RequestBody RequestEnpty enpty) {
        //模板消息跳转小程序--暂未实现
        Map<String, String> miniprogram = new HashMap<>();
        miniprogram.put("appid", Const.APPID);
        miniprogram.put("pagepath", "");
        //获取openid的数据构建的data  使用unionid可以取消(后面接口重构之后再做
        long time = System.currentTimeMillis();
        String str = "qingchunyouyue";
        String secret = DigestUtils.shaHex(DigestUtils.shaHex(String.valueOf(time)) + DigestUtils.md5Hex(str) + "redrock");
        Map<String, Object> data = new HashMap<>();
        data.put("string", str);
        data.put("timestamp", time);
        data.put("secret", secret);
        int flag = 0;
        int count = 0;
        //ManageMsg mmsg = new ManageMsg();
        Map<String,Object> map = new HashMap<>();
        for (Integer cid : enpty.getId()
                ) {
            count++;
            UserMessage user = infoService.finduserinfobycid(cid);
            if(!user.getResult().equals("拉黑")) {
                infoService.updateCbyid(cid, enpty.getBeizhu(), enpty.getResult());
                user.setResult(enpty.getResult());
                Information inf = new Information();
                inf.setCid(cid);
                //依旧还是需要放在里面
                //            String ninfo = getInfo(user,enpty);
                //            inf.setInfo(ninfo);
                if (enpty.getChoose() == 1) {
                    //infoService.add(inf);
                    String ninfo = getInfo(user, enpty);
                    inf.setInfo(ninfo);
                    String[] phone = {user.getPhonenum()};
//                    if (enpty.getTid().equals("205662")) {
//                        if (user.getOname().equals(user.getDname())) {
//                            enpty.setTid("201497");
//                        }
//                    }
                    infoService.updateC0(cid);
                    List<String> info = setInfoList(user, enpty);
                    if (smsService.sendSms(phone, info, Integer.parseInt(enpty.getTid()), cid, inf , user.getOname())) {
                        flag++;
                    }
                } else if (enpty.getChoose() == 0) {
                    //infoService.add(inf);
                    String ninfo = getInfo(user, enpty);
                    inf.setInfo(ninfo);
                    data.put("stuId", user.getStuid());
                    try {
                        JSONObject json = JSONObject.parseObject(CurlUtil.getContent("https://wx.idsbllp.cn/game/api/accesstoken.php", map, "GET"));
                        String token = json.getString("data");  //微信凭证，access_token
                        infoService.updateC0(cid);
                        if (weChatService.WeChatPost(user, data, token, miniprogram, enpty.getInfo(), enpty.getTid(), cid, inf)) {
                            flag++;
                        }
                    }catch (Exception e){
                        //重试获取token -接口偶尔出现获取失败的情况
                        logger.error("Get token report error:"+e);
                        JSONObject json = JSONObject.parseObject(CurlUtil.getContent("https://wx.idsbllp.cn/game/api/accesstoken.php", map, "GET"));
                        String token = json.getString("data");
                        infoService.updateC0(cid);
                        if (weChatService.WeChatPost(user, data, token, miniprogram, enpty.getInfo(), enpty.getTid(), cid, inf)) {
                            flag++;
                        }
                    }

                } else if(enpty.getChoose() == 2){//redrock专属定制
                    //结果并没有（雾
                } else {
                    flag++;
                }
            }else {
                flag++;
            }
        }
        PostResult postResult = new PostResult();
        postResult.setSuccess(flag);
        postResult.setFail(count-flag);
        postResult.setTotal(count);
        return new ResponseEntity<>(postResult, HttpStatus.OK);
    }

    /**
     * 获取发送短信的条数
     * @param request
     * @return
     */
    @BackJwt
    @PostMapping("/469bba0a564235dfceede42db14f17b0/getfee")
    public ResponseEntity<Map> getFee(HttpServletRequest request){
        String accessToken = request.getHeader(ACCESS_TOKEN);
        Claims claims = JwtHelper.parseJWT(accessToken);
        String name = claims.getId();
        return new ResponseEntity<Map>(infoService.getfee(name),HttpStatus.OK);
    }

//    @BackJwt //暂时不用
//    @PostMapping("/469bba0a564235dfceede42db14f17b0/deleteuser")
//    public ResponseEntity<String> Userdelete(@RequestParam Integer cid) {
//        if (msgService.deletebyid(cid) | msgService.deleteInfo(cid)) {
//                return new ResponseEntity<>("success", HttpStatus.OK);
//        }
//        return new ResponseEntity<>("fail", HttpStatus.valueOf(500));
//
//    }

    /**
     * 获取当前组织报名人次
     * @param request
     * @param oname --不需求 测试用
     * @return
     */
    @BackJwt
    @PostMapping("/469bba0a564235dfceede42db14f17b0/systemcount")
    public ResponseEntity<Integer> AllCount(HttpServletRequest request , @RequestParam(required = false) String oname) {
        String accessToken = request.getHeader(ACCESS_TOKEN);
        Claims claims = JwtHelper.parseJWT(accessToken);
        String joname = claims.getId();
        if(joname.equals("")) {//测试用jwt预留
            if(oname.equals("")){
                return new ResponseEntity<>(-1, HttpStatus.valueOf(500));
            }
            return new ResponseEntity<>(infoService.allcount(oname), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(infoService.allcount(joname), HttpStatus.OK);
        }
    }

    /**
     * 获取当前组织的报名人数
     * @param request
     * @param oname -- 不需求 测试用
     * @return
     */
    @BackJwt
    @PostMapping("/469bba0a564235dfceede42db14f17b0/usercount")
    public ResponseEntity<Integer> UserCount(HttpServletRequest request ,@RequestParam(required = false) String oname) {
        String accessToken = request.getHeader(ACCESS_TOKEN);
        Claims claims = JwtHelper.parseJWT(accessToken);
        String joname = claims.getId();
        if(joname.equals("")) {
            if(oname.equals("")){
                return new ResponseEntity<>(-1, HttpStatus.valueOf(500));
            }
            return new ResponseEntity<>(infoService.usercount(oname), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(infoService.usercount(joname), HttpStatus.OK);
        }
    }

    /**
     * 查询相应数据下的总数
     * @param request
     * @param oname
     * @param dname
     * @param info
     * @param result
     * @return
     */
    @BackJwt
    @PostMapping("/469bba0a564235dfceede42db14f17b0/basecount")
    public ResponseEntity<Integer> BaseCount(HttpServletRequest request ,@RequestParam(required = false) String oname, @RequestParam(required = false) String dname, @RequestParam(required = false) String info, @RequestParam(required = false) String result) {
        String accessToken = request.getHeader(ACCESS_TOKEN);
        Claims claims = JwtHelper.parseJWT(accessToken);
        String joname = claims.getId();
        if(joname.equals("")) {
            if(oname.equals("")){
                return new ResponseEntity<>(-1, HttpStatus.valueOf(500));
            }
            return new ResponseEntity<>(infoService.ocount(oname, dname, info, result), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(infoService.ocount(joname, dname, info, result), HttpStatus.OK);
        }
    }

    /**
     * 分页获取页码 可限定分页大小size
     * @param request
     * @param oname
     * @param size
     * @param dname
     * @param info
     * @param result
     * @param stuname
     * @param stuid
     * @param college
     * @param gender
     * @return
     */
    @BackJwt
    @PostMapping("/469bba0a564235dfceede42db14f17b0/gettotal")
    public ResponseEntity<Integer> gettotal(HttpServletRequest request ,@RequestParam(required = false) String oname,@RequestParam Integer size , @RequestParam(required = false) String dname, @RequestParam(required = false) String info, @RequestParam(required = false) String result, @RequestParam(required = false) String stuname , @RequestParam(required = false) String stuid,@RequestParam(required = false) String college,@RequestParam(required = false) String gender) {
        String accessToken = request.getHeader(ACCESS_TOKEN);
        Claims claims = JwtHelper.parseJWT(accessToken);
        String joname = claims.getId();
        ManageMsg mmsg = new ManageMsg();
        mmsg.setOname((joname.equals(""))?oname:joname);
        mmsg.setDname(dname);
        mmsg.setInfo(info);
        mmsg.setResult(result);
        mmsg.setSize(size);
        mmsg.setStuid(stuid);
        mmsg.setCollege(college);
        mmsg.setGender(gender);
        Double Dsize = Double.valueOf(size);
        mmsg.setStuname(stuname);
        return new ResponseEntity<>((int)Math.ceil(infoService.getsize(mmsg)/Dsize), HttpStatus.OK);
    }

    /**
     * 分页获取用户信息 通过size限定分页大小
     * @param request
     * @param oname
     * @param size
     * @param currindex
     * @param dname
     * @param info
     * @param result
     * @param stuname
     * @param stuid
     * @param college
     * @param gender
     * @return
     */
    @BackJwt
    @PostMapping("/469bba0a564235dfceede42db14f17b0/getuserlist")
    public ResponseEntity<List<ManageMsg>> getUserList(HttpServletRequest request ,@RequestParam(required = false)  String oname,@RequestParam Integer size, @RequestParam Integer currindex, @RequestParam(required = false) String dname, @RequestParam(required = false) String info, @RequestParam(required = false) String result, @RequestParam(required = false) String stuname, @RequestParam(required = false) String stuid,@RequestParam(required = false) String college,@RequestParam(required = false) String gender) {
        String accessToken = request.getHeader(ACCESS_TOKEN);
        Claims claims = JwtHelper.parseJWT(accessToken);
        String joname = claims.getId();
        ManageMsg mmsg = new ManageMsg();
        mmsg.setOname((joname.equals(""))?oname:joname);
        mmsg.setDname(dname);
        mmsg.setInfo(info);
        mmsg.setResult(result);
        mmsg.setSize(size);
        mmsg.setStuname(stuname);
        mmsg.setStuid(stuid);
        mmsg.setCollege(college);
        mmsg.setGender(gender);
        if (currindex < 1) {
            List<ManageMsg> msg = new ArrayList<>();
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        mmsg.setCurrIndex(currindex);
        return new ResponseEntity<>(infoService.findchoose(mmsg), HttpStatus.OK);
    }

    /**
     * 获得总的info
     * @param oname
     * @param dname
     * @return
     */
    @BackJwt
    @PostMapping("/469bba0a564235dfceede42db14f17b0/getcinfo")
    public ResponseEntity<List<String>> getCInfo(@RequestParam(required = false) String oname , @RequestParam(required = false) String dname) {
        return new ResponseEntity<>(infoService.getcinfolist(oname , dname), HttpStatus.OK);
    }

//    /**
//     * 单独推送接口 (*后台用
//     * @param enpty
//     * @return
//     */
//    @PostMapping("/469bba0a564235dfceede42db14f17b0/wechat")
//    public ResponseEntity<PostResult> wechatPost(@RequestBody RequestEnpty enpty){
//        Map<String, String> miniprogram = new HashMap<>();
//        miniprogram.put("appid", Const.APPID);
//        miniprogram.put("pagepath", "");
//        long time = System.currentTimeMillis();
//        String str = "qingchunyouyue";
//        String secret = DigestUtils.shaHex(DigestUtils.shaHex(String.valueOf(time)) + DigestUtils.md5Hex(str) + "redrock");
//        Map<String, Object> data = new HashMap<>();
//        data.put("string", str);
//        data.put("timestamp", time);
//        data.put("secret", secret);
//        int flag = 0;
//        int count = 0;
//        //ManageMsg mmsg = new ManageMsg();
//        Map<String,Object> map = new HashMap<>();
//        for (Integer sid : enpty.getId()
//                ) {
//            count++;
//            data.put("stuId", sid);
//            User user = userService.findBySidtoprize(String.valueOf(sid));
//            UserMessage userM = new UserMessage();
//            userM.setStuname(user.getStuname());
//            userM.setStuid(sid);
//            try {
//                JSONObject json = JSONObject.parseObject(CurlUtil.getContent("https://wx.idsbllp.cn/game/api/accesstoken.php", map, "GET"));
//                String token = json.getString("data");  //微信凭证，access_token
//                if (weChatService.WeChatPost(userM, data, token, miniprogram, enpty.getInfo(), enpty.getTid(), -1, null)) {
//                    flag++;
//                }
//            }catch (Exception e){
//                logger.error("微信推送获取token报错:"+e);
//                JSONObject json = JSONObject.parseObject(CurlUtil.getContent("https://wx.idsbllp.cn/game/api/accesstoken.php", map, "GET"));
//                String token = json.getString("data");  //微信凭证，access_token
//                if (weChatService.WeChatPost(userM, data, token, miniprogram, enpty.getInfo(), enpty.getTid(), -1, null)) {
//                    flag++;
//                }
//            }
//        }
////        JSONObject jsonObject = new JSONObject();
////        jsonObject.put("总数",count);
////        jsonObject.put("成功",flag);
////        jsonObject.put("失败",count-flag);
//        PostResult postResult = new PostResult();
//        postResult.setSuccess(flag);
//        postResult.setFail(count-flag);
//        postResult.setTotal(count);
//        return new ResponseEntity<>(postResult, HttpStatus.OK);
//    }

    /**
     * 给小程序推送的消息 (* 要求和微信消息模板类似
     * @param user
     * @param enpty
     * @return
     */
    public String getInfo(UserMessage user , RequestEnpty enpty){
        String str = "";
        if(enpty.getTid().equals("H3VNgVqo3r9ewRi0hhGJDKl_-VBginnIgtFmNyRXeiM") ){
            str = "亲爱的"+user.getStuname()+"同学,你好，你先前的申请的社团组织需要"+enpty.getInfo().get(0)+"，现将详细面试信息发给你。\n" +
                    "社团组织:"+(user.getOname().equals(user.getDname())?user.getOname():user.getOname()+user.getDname())+"\n" +
                    "面试地点:"+enpty.getInfo().get(1)+"\n" +
                    "面试时间:"+enpty.getInfo().get(2)+"\n" +
                    "联系人姓名:"+enpty.getInfo().get(3)+"\n" +
                    "联系人联系方式:"+enpty.getInfo().get(4);
            //+"\n" +
//                    enpty.getInfo().get(5) +
//                    "记得准时参加哦~";
        } else if(enpty.getTid().equals("196810") | enpty.getTid().equals("ptsau_vXeAlzsRubHLqaxlFkyicvDbMUJLGCXdniK_g")) {
            str = "来自"+user.getOname()+"的提醒:\n" +
                    "学生姓名:"+user.getStuname()+"\n" +
                    "意向部门:"+user.getDname()+"\n" +
                    "录取结果:"+enpty.getInfo().get(0);//+"\n" +
                    //enpty.getInfo().get(1);
        }else if(enpty.getTid().equals("205662")){
            str = "亲爱的"+user.getStuname()+"同学,你好，你先前的申请的社团组织需要"+enpty.getInfo().get(0)+"，现将详细面试信息发给你。\n" +
                    "社团组织:"+user.getOname()+(user.getOname().equals(user.getDname())?"":user.getDname())+"\n" +
                    "地点:"+enpty.getInfo().get(1)+enpty.getInfo().get(2)+"\n" +
                    "时间:"+enpty.getInfo().get(3)+"\n" +
                    "联系人联系方式:"+enpty.getInfo().get(4)+enpty.getInfo().get(5);
        }else if(enpty.getTid().equals("test")){//技术部门定制
            str = "亲爱的"+user.getStuname()+"同学，菌菌通知您参加红岩网校工作站技术部门统一笔试！\n" +
                    "部门："+user.getDname()+"，\n" +
                    "笔试时间：10月15日 "+enpty.getInfo().get(0)+"，\n" +
                    "考场："+enpty.getInfo().get(1)+"，期待您取得好成绩ʕ ᵔᴥᵔ ʔ  ，\n" +
                    "如果时间冲突，请在"+enpty.getInfo().get(2)+"前往4教4楼安排考场。如两场都冲突，请加Q群852716386";
        }else if(enpty.getTid().equals("210276")){//产品部门定制
            str = user.getStuname()+"同学，欢迎欢迎报名红岩网校工作站产品策划及运营部，菌菌通知您来参加面试哦!\n" +
                    "地点:太极操场西六门三楼，\n" +
                    "时间: 10月15日16点至22点:16日- -17日18至22点，\n" +
                    "联系方式:菌菌-13098776339";
        }else if(enpty.getTid().equals("210354")){//视觉部门定制
            str = "亲爱的"+user.getStuname()+"同学，你在红岩网校工作站视觉设计部的报名成功了哦，请来参加面试哦～\n" +
                    "时间为：10月15日～16日 8:00-21:00 。\n" +
                    "地点：太极西三门右转走至尽头后上楼 红岩网校b区会议室。期待与大家见面~";
        } else {
            throw  new RuntimeException("Template message format error!");
        }
        return str;
    }

    /**
     * 设置手机短信推送的info
     * @param user
     * @param enpty
     * @return
     */
    public List<String> setInfoList(UserMessage user , RequestEnpty enpty){
        List<String> info = new ArrayList<>();
        if(enpty.getTid().equals("205662")){
                info.add(user.getStuname());
                info.add(enpty.getInfo().get(0));
                info.add(user.getOname());
                info.add((user.getOname().equals(user.getDname()) ? "" : user.getDname()));
                info.add(enpty.getInfo().get(1));
                info.add(enpty.getInfo().get(2));
                info.add(enpty.getInfo().get(3));
                info.add(enpty.getInfo().get(4));
                info.add(enpty.getInfo().get(5));
            //info.add(enpty.getInfo().get(4));
        } else if(enpty.getTid().equals("196810")) {
            info.add(user.getOname());
            info.add(user.getStuname());
            info.add(user.getDname());
            info.add(enpty.getInfo().get(0));
            //info.add(enpty.getInfo().get(3));
        }else if(enpty.getTid().equals("201497")){
            info.add(user.getStuname());
            info.add(enpty.getInfo().get(0));
            info.add(user.getOname());
            info.add(enpty.getInfo().get(1));
            info.add(enpty.getInfo().get(2));
            info.add(enpty.getInfo().get(3));
        }else if (enpty.getTid().equals("test")){
            info.add(user.getStuname());
            info.add(user.getDname());
            info.add(enpty.getInfo().get(0));
            info.add(enpty.getInfo().get(1));
            info.add(enpty.getInfo().get(2));
        }else if(enpty.getTid().equals("210276")){//产品定制
            info.add(user.getStuname());
            info.add("参加面试");
        }else if (enpty.getTid().equals("210354")){//视觉定制
            info.add(user.getStuname());
            info.add("参加面试");
        } else {
            throw  new RuntimeException("Template message format error!");
        }
        return info;
    }


//    @BackJwt
//    @PostMapping("/469bba0a564235dfceede42db14f17b0/getcres")
//    public ResponseEntity<List<String>> getCRes() {
//        return new ResponseEntity<>(infoService.getcreslist(), HttpStatus.OK);
//    }

//    public static void main(String[] args) {
//        String str1 = "红岩网校工作站";
//        String str2 = "红岩网校工作站1";
//        System.out.println(str1.equals(str2)?str1:str1+str2);
//    }
}
