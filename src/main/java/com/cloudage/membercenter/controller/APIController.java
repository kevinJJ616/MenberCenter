package com.cloudage.membercenter.controller;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudage.membercenter.entity.User;
import com.cloudage.membercenter.service.IUserService;


@RestController
@RequestMapping("/api")
public class APIController {
	 
	@Autowired
	IUserService userService;

	@RequestMapping(value = "/hello", method=RequestMethod.GET)
	public @ResponseBody String hello(){
		return "HELLO WORLD";
	}

	@RequestMapping(value="/register",method=RequestMethod.POST)
	public User register(
			@RequestParam String account,
			@RequestParam String passwordHash,
			@RequestParam String email,
			@RequestParam String name,
			MultipartFile avatar,
			HttpServletRequest request){
		
		User user =new User();
		user.setAccount(account);
		user.setPasswordHash(passwordHash);
		user.setEmail(email);
		user.setName(name);
		
		if(avatar!=null){
			try{
			String realPath=request.getSession().getServletContext().getRealPath("/WEB-INF/upload");
		FileUtils.copyInputStreamToFile(avatar.getInputStream(), new File(realPath,account+".png"));
		user.setAvatar("upload/"+account+".png");
		}catch (Exception e) {
			e.printStackTrace();			
			}
		
		}
		return userService.save(user);
	}
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public User login(
			@RequestParam String account,
			@RequestParam String passwordHash,
			HttpServletRequest request){
		
		User user=userService.findByAccount(account);
		if(user!=null&&user.getPasswordHash().equals(passwordHash)){
//			request.getSession().setAttribute("current_user",user);
			HttpSession session=request.getSession(true);
			session.setAttribute("uid",user.getId());
			return user;
		}else
			return null;				
	}
	
	@RequestMapping(value="/me",method=RequestMethod.GET)
	public User getCurrentUser(HttpServletRequest request){
//		Object obj=request.getSession().getAttribute("current_user");
//		if(obj instanceof User){
//			return (User) obj;
//		}else{
//			return null;
//		}
		HttpSession session=request.getSession(true);
		Integer uid=(Integer) session.getAttribute("uid");
		return userService.findById(uid);
	}
	
	
}
