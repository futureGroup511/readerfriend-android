package com.example.Main;

public class Config {
	public static String SERVER_IP="https://three.jfree.top/";//������ip
	public static String PREFIX="readerfriend/app/manager/";//������ip
	public static final String METHOD_LOGIN ="login";//���ʵķ�����
	public static final String Check_Number ="inputVCode";//���ʵķ�����
	public static final String PING_TOKEN ="pingToken";//���ʵķ�����
	public static final String BORROWBOOK = "userBorrowBook";//����
	public static final String RETURNBOOK ="returnBook";//����
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
