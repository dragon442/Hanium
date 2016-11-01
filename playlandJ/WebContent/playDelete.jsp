<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<meta name="viewport" content="width=device-width,initial-scale=1.0"/>
<% request.setCharacterEncoding("utf-8"); %>

<%
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	try {
		String jdbcUrl = "jdbc:mysql://localhost:3306/playland";
		String dbId="landid";
		String dbPass="landpass";
		
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(jdbcUrl, dbId, dbPass);
		
		for(int i=1; i<=3; i++) {
			String sql = "delete from play" + i + " where rid_time < now()";			//delete from [테이블명] (where [조건])
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			//out.println("play" + i + " 테이블의 레코드를 삭제했습니다.<br>");
		}
				
	} catch(Exception e) {
		e.printStackTrace();
	} finally {
		if(rs != null) try{rs.close();}catch(SQLException sqle){}
		if(pstmt != null) try{pstmt.close();}catch(SQLException sqle){}
		if(conn != null) try{conn.close();}catch(SQLException sqle){}
	}

%>