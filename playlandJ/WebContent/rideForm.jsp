<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<meta name="viewport" content="width=device-width,initial-scale=1.0"/>
<link rel="stylesheet" href="style.css"/>

<form method="post" action="ride.jsp">
      <legend>탑승 할 놀이기구</legend>
      <input id="play" name="play" type="radio" value="play1" checked>
      <label for="play">play1</label>
      <input id="play" name="play" type="radio" value="play2">
      <label for="play">play2</label>
      <input id="play" name="play" type="radio" value="play3">
      <label for="play">play3</label>
	<table>
		<tr>
		 <td class="labeL"><label for="id">아이디</label>
		 <td class="content"><input id="id" name="id" type="text" size="20"
		 	maxlength="50" placeholder="example" autofocus required>
		<!--
		<tr>
		 <td class="label"><label for="nomality">정상인</label>
		 <td class="content"><input id="nomality" name="nomality" type="text" size="20"
		 	maxlength="10" placeholder="t/f" required>
		<tr>
		 <td class="label"><label for="accom_num">동행인 수</label>
		 <td class="content"><input id="accom_num" name="accom_num" type="text" size="20"
		  	maxlength="100" placeholder="n" required>  -->
		 <tr>
		  <td class="label2" colspan="2"><input type="submit" value="입력완료">
		  	<input type="reset" value="다시작성">
	</table>
</form>
