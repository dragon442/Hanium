<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.sql.*" import="java.util.Date" import="java.text.SimpleDateFormat" %>

<jsp:include page="playDelete.jsp"  flush="false"/>

<%
request.setCharacterEncoding("utf-8");

String id = request.getParameter("id");
//String nomality = request.getParameter("nomality");
//String accom_num = request.getParameter("accom_num");
String play = request.getParameter("play");

Timestamp reg_time = new Timestamp(System.currentTimeMillis());
long wating=0;		//기다려야 하는 시간
int interval=0;		//놀이기구 한 타임 운영 시간
int k = play.charAt(4);		//몇번쨰 놀이기구인지
k = k-48;
switch(k) {
	case 1 : interval=3;
		break;
	case 2 : interval=2;
		break;
	case 3 : interval=4;
		break;
}

//int acm_num = Integer.parseInt(accom_num);
int num=0;

Connection conn = null;
PreparedStatement pstmt = null;
ResultSet rs = null;

try{
	String jdbcUrl="jdbc:mysql://localhost:3306/playland";
	String dbId="landid";
	String dbPass="landpass";
	
	Class.forName("com.mysql.jdbc.Driver");
	conn = DriverManager.getConnection(jdbcUrl, dbId, dbPass);
	
	String sql_sel = "select * from " + play.trim();
	pstmt = conn.prepareStatement(sql_sel);
	rs = pstmt.executeQuery();

	rs.last();
	int count = rs.getRow();
	num = ++count;
	rs.beforeFirst();
	switch(k) {
		case 1 : count = count/3 + 1;		//예약시 기본적으로 놀이기구가 운행중이라고 가정
			break;
		case 2 : count = count/2 + 1;		//2인승 놀이기구면 2명이 예약해야 시간 사이클 1회증가 
			break;
		case 3 : count = count/4 + 1;
			break;
	}
	wating = (long)count*interval*60*1000; 	//1회 운영시 1번, 2번, 3번 각 3, 2, 4분에 탑승인원 3, 2, 4로 가정
	
	Timestamp rid_time = new Timestamp(System.currentTimeMillis() + wating);

	String sql_ins = "insert into " + play.trim() + " values (?,?,?)";
	pstmt = conn.prepareStatement(sql_ins);
	pstmt.setString(1, id);
	pstmt.setTimestamp(2, reg_time);
	pstmt.setTimestamp(3, rid_time);
	//pstmt.setString(4, nomality);
	pstmt.executeUpdate();
	

	out.println("member 테이블에 새로운 레코드를 추가했습니다");
	
} catch(Exception e) {
	e.printStackTrace();
	out.println("member 테이블에 새로운 레코드 추가에 실패헀습니다");
} finally {
	//리소스들을 해제해줌
	if(pstmt != null) try { pstmt.close();} catch(SQLException sqle) {}
	if(conn != null) try{conn.close();} catch(SQLException sqle) {}
}

%>
