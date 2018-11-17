package org.redrock.activityplatform;

import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.redrock.activityplatform.controller.UserController;
import org.redrock.activityplatform.core.util.CurlUtil;
import org.redrock.activityplatform.data.domain.Information;
import org.redrock.activityplatform.data.domain.Manage;
import org.redrock.activityplatform.data.domain.ManageMsg;
import org.redrock.activityplatform.data.domain.User;
import org.redrock.activityplatform.data.service.InfoService;
import org.redrock.activityplatform.data.service.MsgService;
import org.redrock.activityplatform.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan("org.redrock.activityplatform.data.dao")
public class ActivityplatformApplicationTests {

	@Resource
	private InfoService infoService;

	private MockMvc mockMvc; // 模拟MVC对象，通过MockMvcBuilders.webAppContextSetup(this.wac).build()初始化。

	@Autowired
	private WebApplicationContext wac; // 注入WebApplicationContext


	@Before // 在测试开始前初始化工作
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	@Test
	public void contextLoads() {
	}

	@Test
	public void test() throws Exception {

//		MvcResult result = mockMvc.perform(post("/save").contentType(MediaType.APPLICATION_JSON).content(JSONObject.toJSONString(map)))
//				.andExpect(status().isOk())// 模拟向testRest发送get请求
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
//				.andReturn();// 返回执行请求的结果
//
//		System.out.println(result.getResponse().getContentAsString());

		MvcResult result = mockMvc.perform(post("/page").param("pageNo", "1").param("pageSize", "2"))
				.andExpect(status().isOk())// 模拟向testRest发送get请求
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8
				.andReturn();// 返回执行请求的结果

		System.out.println(result.getResponse().getContentAsString());
//		String a = "ouRCyji8D0MDjOtEZ4eQNHHb3mDY"+"/"+"hy"+"/"+"gg";
//		String[] aa = a.split("/");
//		System.out.println(aa[2]);
//		ManageMsg mmsg = new ManageMsg();
//		mmsg.setOname("红岩网校工作站");
//		mmsg.setStuname("");
//		mmsg.setInfo("");
//		mmsg.setCurrIndex(1);
//		System.out.println(infoService.findchoose(mmsg));
//		Manage manage = new Manage();
//		manage.setName("红岩网校工作站");
//		manage.setPassword("123456");
//		System.out.println(infoService.login(manage));
//		Integer cid = 65;
//		Information inf = new Information();
//		User user = infoService.finduserinfobycid(cid);
//		List<String> info = new ArrayList<>();
//		info.add("oname");
//		info.add("dname");
//		info.add("time");
//		info.add("addr");
//		info.add("man");
//		info.add("13000991231");
//		String ninfo = "亲爱的"+user.getStuname()+"同学,你好\n" +
//				"这是一则面试通知\n" +
//				"面试职位:"+info.get(1)+"\n" +
//				"所属公司:"+info.get(0)+"\n" +
//				"面试时间:"+info.get(2)+"\n" +
//				"面试地点:"+info.get(3)+"\n" +
//				"联系人:"+info.get(4)+"\n" +
//				"联系电话:"+info.get(5)+"\n" +
//				"记得准时参加哦~";
//		inf.setInfo(ninfo);
//		inf.setCid(cid);
//		infoService.add(inf);
//		Map<String,Object> map = new HashMap<>();
//		JSONObject json = JSONObject.parseObject(CurlUtil.getContent("https://wx.idsbllp.cn/game/api/accesstoken.php",map,"GET"));
//		System.out.println(json.getString("data"));  //微信凭证，access_token
	}
}
