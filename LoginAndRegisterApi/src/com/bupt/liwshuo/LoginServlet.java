package com.bupt.liwshuo;

import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 处理登陆请求
 * Created by shuo on 2015/4/7.
 */
@WebServlet(name = "LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tag = request.getParameter("tag");
        DBManager dbManager = new DBManager();
        JSONObject result = new JSONObject();
        result.put("tag", tag);
        System.out.println(tag);

        if (tag.equals("login")) {
            String email = request.getParameter("email");//从请求中取出邮箱
            String password = request.getParameter("password");//从请求中取出密码
            JSONObject validUser = dbManager.getUserByEmailAndPassword(email, password);//通过邮箱和密码获取用户的信息，如果不存在则为null
            if (validUser != null) {
                result.put("user", validUser);
                result.put("error", false);
            } else {
                result.put("error", true);
                result.put("error_msg", "邮箱或密码错误");
            }
        } else {
            result.put("error", true);
            result.put("error_msg", "tag必须是login或register");
        }
        System.out.println(result.get("error_msg"));
        //设置response的格式并写入返回信息
        response.setContentType("text/html; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.println(result.toString());
        printWriter.flush();
        printWriter.close();
        dbManager.closeDB();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
