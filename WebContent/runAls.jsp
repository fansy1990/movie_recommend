<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>电影在线推荐系统#建模</title>
</head>
<body>
	<div style="padding-top: 50px; text-align: center">
		<div>
			<h2>电影在线推荐系统#建立模型</h2>
		</div>
		<form action="RunALS" method="get">
		<!-- <input> <output> <train_percent> <ranks> <lambda> <iteration> -->
		<table border="0" align="center" style="padding: 20px">
			<tr >
				<td>输入路径（Rating.dat）：</td>
				<td><input type="text" name="input" id="input_id"></td>
			</tr>
			<tr >
				<td>训练比重（0~1）：</td>
				<td>
					<select name="train_percent" id="train_percent_id">
						<option value="0.9" selected="selected">0.9</option>
						<option value="0.8">0.8</option>
						<option value="0.7" >0.7</option>
						<option value="0.6">0.6</option>
						<option value="0.5">0.5</option>
						<option value="0.4" >0.4</option>
						<option value="0.3">0.3</option>
					</select>
				</td>
			</tr>
			<tr >
				<td>矩阵分解秩：</td>
				<td>
					<select name="ranks" id="ranks_id">
						<option value="8" selected="selected">8</option>
						<option value="9">9</option>
						<option value="10" >10</option>
						<option value="11">11</option>
						<option value="12">12</option>
					</select>
				</td>
			</tr>
			<tr >
				<td>正则系数：</td>
				<td>
					<select name="lambda" id="lambda_id">
						<option value="0.1">0.1</option>
						<option value="0.2">0.2</option>
						<option value="0.3">0.3</option>
						<option value="0.4">0.4</option>
						<option value="0.5">0.5</option>
						<option value="0.6">0.6</option>
						<option value="0.7">0.7</option>
						<option value="0.8">0.8</option>
						<option value="0.9">0.9</option>
						<option value="1.0" selected="selected">1.0</option>
						<option value="2.0">2.0</option>
						<option value="3.0">3.0</option>
						<option value="4.0">4.0</option>
						<option value="5.0">5.0</option>
						<option value="6.0">6.0</option>
						<option value="7.0">7.0</option>
						<option value="8.0">8.0</option>
						<option value="9.0">9.0</option>
						<option value="10.0">10</option>
					</select>
				</td>
			</tr>
			
			<tr >
				<td>循环次数：</td>
				<td>
					<select name="iteration" id="iteration_id">
						<option value="20" selected="selected">20</option>
						<option value="9">9</option>
						<option value="10" >10</option>
						<option value="11">11</option>
						<option value="12">12</option>
						<option value="19">19</option>
						<option value="15" >15</option>
						<option value="21">21</option>
						<option value="30">30</option>
					</select>
				</td>
			</tr>
			
			<tr style="text-align: left	">
				<td><input type="submit" value="建模" ></td>
			</tr>
		</table>
		</form>
	</div>
	
</body>
</html>