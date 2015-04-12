package com.bupt.liwshuo;

import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**处理注册请求
 * Created by shuo on 2015/4/9.
 */
@WebServlet(name = "RegisterServlet")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tag = request.getParameter("tag");
        DBManager dbManager = new DBManager();
        JSONObject result = new JSONObject();
        result.put("tag", tag);

        if (tag.equals("register")) {
            String name = request.getParameter("name");//从请求中取出姓名
            String email = request.getParameter("email");//从请求中取出邮箱
            String password = request.getParameter("password");//从请求中取出密码
            //判断用户是否存在，如果存在返回错误信息，如果不存在，则进行注册
            if (dbManager.isUserExisted(email)) {
                result.put("error", true);
                result.put("error_msg", "用户已存在");
            } else {
                JSONObject user = new JSONObject();
                user.put("name", name);
                user.put("email", email);
                user.put("password", password);
                JSONObject validUser = dbManager.storeUser(user);
                if (validUser != null) {
                    result.put("error", false);
                    result.put("user", validUser);
                } else {
                    result.put("error", true);
                    result.put("error_msg", "注册失败");
                }
            }
        } else {
            result.put("error", true);
            result.put("error_msg", "tag必须是login或register");
        }
        //设置response的格式，并且将返回信息写入
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
