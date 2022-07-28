package jp.co.internous.sunny.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import jp.co.internous.sunny.model.domain.MstUser;
import jp.co.internous.sunny.model.form.UserForm;
import jp.co.internous.sunny.model.mapper.MstUserMapper;
import jp.co.internous.sunny.model.mapper.TblCartMapper;
import jp.co.internous.sunny.model.session.LoginSession;

@RestController
@RequestMapping(value = "/sunny/auth")
public class AuthController {
	
	private Gson gson = new Gson();
	
	@Autowired
	private LoginSession loginSession;
	
	@Autowired
	private MstUserMapper mstUserMapper;
	
	@Autowired
	private TblCartMapper tblCartMapper;
	
	@RequestMapping(value = "/login")
	public String login(@RequestBody UserForm form) {
		
		MstUser user = mstUserMapper.findByUserNameAndPassword(form.getUserName(), form.getPassword());
		
		int tmpUserId = loginSession.getTmpUserId();
		
		if (user  != null && tmpUserId != 0) {
				int count = tblCartMapper.findCountByUserId(tmpUserId);
				if (count > 0) {
					tblCartMapper.updateUserId(user.getId(), tmpUserId);
				}
		}
		
		if (user != null) {
			loginSession.setUserId(user.getId());
			loginSession.setTmpUserId(0);
			loginSession.setUserName(user.getUserName());
			loginSession.setPassword(user.getPassword());
			loginSession.setLogined(true);
		} else {
			loginSession.setUserId(0);
			loginSession.setUserName(null);
			loginSession.setPassword(null);
			loginSession.setLogined(false);
		}
		return gson.toJson(user);
	}
	
	@RequestMapping(value = "/logout")
	public String logout() {
		
		loginSession.setUserId(0);
		loginSession.setTmpUserId(0);
		loginSession.setUserName(null);
		loginSession.setPassword(null);
		loginSession.setLogined(false);
		
		return "";
	}
	

}
