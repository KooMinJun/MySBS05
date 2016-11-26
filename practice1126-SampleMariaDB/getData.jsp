<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.sql.*"%>
<%
String userId = request.getParameter("userId");
String userPw = request.getParameter("userPw");

System.out.println("userId>>>" + userId);
System.out.println("userPw>>>" + userPw);

out.println("out.println userId>>>" + userId + "<br/>");
out.println("out.println userPw>>>" + userPw + "<br/>");

Connection conn = null; 
Statement stmt = null; //보안에취약함

try{
    Class.forName("com.mysql.jdbc.Driver");
    //test는 데이터베이스 파일이름, "마지막인자는 testpw"
    conn = DriverManager.getConnection("jdbc:mysql://192.168.27.6/test", "root", "Alswnse33*");
    if(conn == null){
	throw new Exception("데이터베이스에 연결할 수 없습니다");
    }
    stmt = conn.createStatement();
    //데이터베이스에 명령할 구문임
    String sql = String.format("INSERT INTO member(userId, userPw) values('"+userId+"', '"+userPw+"');");
    int rowNum = stmt.executeUpdate(sql);
    if(rowNum < 1){
        throw new Exception("데이터베이스에 값을 입력할 수 없습니다.");
    }
}catch(Exception e){

}

//해제는 역순 (stmt->conn순)
finally{
    try{
	stmt.close(); //찌꺼기 없애주는것

    }catch(Exception ignored){}
    try{
	conn.close(); //찌꺼기 없애주는것

    }catch(Exception ignored){}
}
%>