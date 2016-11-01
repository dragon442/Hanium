<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<meta name="viewport" content="width=device-width,initial-scale=1.0"/>
<link rel="stylesheet" href="style.css"/>

<jsp:include page="playDelete.jsp"  flush="false"/>

<table>
	<tr class="label">
	 <td>아이디
	 <td>예약자 수
	 <td>예약시간
	 <td>예상탑승시간

<%
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	try {
		String jdbcUrl="jdbc:mysql://localhost:3306/playland";
		String dbId="landid";
		String dbPass="landpass";
		
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(jdbcUrl, dbId, dbPass);
		
		for(int i=1; i<=3; i++) {
			String sql_sel = "select * from play" + String.valueOf(i);
			pstmt = conn.prepareStatement(sql_sel);
			rs = pstmt.executeQuery();

			rs.last();
			int accom_num = rs.getRow();
			
			out.println("<tr><td colspan=\"4\">play" +i+" 예약현황 ");	//표 가독성을 위해 레코드 표기시 놀이기구마다 한줄씩 띔
			String sql = "select * from play" + String.valueOf(i);	//select [검색 할 레코드] from [테이블명] (where [조건])
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();			//쿼리 실행, ececuteQuery()는 recordSet을 반환하므로 ResultSet과 같이 Select 문에서 사용
			
			while(rs.next()) {				//커서를 다음 행으로 옮김, 다음 행이 있으면 true 없으면 false
				String id = rs.getString("id");
				Timestamp reg = rs.getTimestamp("reg_time");
				Timestamp rid = rs.getTimestamp("rid_time");
				//String nom = rs.getString("nomality");
				//String accom_num = rs.getString("accom_num");
				//System.out.println("reg : " + reg.getTime() + "  rid : " + rid.getTime());
%>
	<tr>
	 <td><%=id %>
	 <td>accom_num<%=accom_num %>
	 <td><%=reg.getTime() %>
	 <td><%=rid.getTime() %>
	 <td><%=reg.toString() %>
	 <td><%=rid.toString() %>
<% 			}
		}
	} catch(Exception e) {
		e.printStackTrace();
	} finally {
		if(rs != null) try{rs.close();}catch(SQLException sqle){}
		if(pstmt != null) try{pstmt.close();}catch(SQLException sqle){}
		if(conn != null) try{conn.close();}catch(SQLException sqle){}
	}
%></table>