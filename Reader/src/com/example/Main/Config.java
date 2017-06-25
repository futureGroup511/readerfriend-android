package com.example.Main;

public class Config {
	public static String SERVER_IP="https://three.jfree.top/";//服务器ip
	public static String PREFIX="readerfriend/app/manager/";//服务器ip
	public static final String METHOD_LOGIN ="login";//访问的方法名
	public static final String Check_Number ="inputVCode";//访问的方法名
	public static final String PING_TOKEN ="pingToken";//访问的方法名
	public static final String BORROWBOOK = "userBorrowBook";//借书
	public static final String RETURNBOOK ="returnBook";//还书
	public static final String PINGTOKEN = "readerfriend/pingToken";
	public static String getSERVER_IP() {
		return SERVER_IP;
	}
	public static void setSERVER_IP(String sERVER_IP) {
		SERVER_IP = sERVER_IP;
	}
	public static String getAllUrl(String url){
		return SERVER_IP+PREFIX+url;
	}
}
