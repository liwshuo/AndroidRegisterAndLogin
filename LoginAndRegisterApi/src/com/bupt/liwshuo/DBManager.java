package com.bupt.liwshuo;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Calendar;
import java.util.UUID;


/**
 * 数据库管理功能
 * Created by shuo on 2015/4/7.
 */
public class DBManager {

    //DBUtils为数据库封装工具
    DBUtils dbUtils = null;
    public DBManager() {
        dbUtils = new DBUtils(Config.DB_JDBC_MYSQL,Config.DB_URL,Config.DB_USER,Config.DB_PASSWORD);//初始化数据库封装工具
        createTable();
    }

    /**
     * 创建表
     */
    public void createTable() {
            String createSql = "create table if not exists users(\n" +
                    "   uid int(11) primary key auto_increment,\n" +
                    "   unique_id varchar(36) not null unique,\n" +
                    "   name varchar(50) not null,\n" +
                    "   email varchar(100) not null unique,\n" +
                    "   encrypted_password varchar(80) not null,\n" +
                    "   salt varchar(10) not null,\n" +
                    "   created_at datetime,\n" +
                    "   updated_at datetime null\n" +
                    ");";
        dbUtils.createTable(createSql);
    }

    /**
     * 保存用户信息
     * @param user
     * @return 用户的json格式信息
     */
    public JSONObject storeUser(JSONObject user) {
        String name = user.getString("name");//获取名字
        String email = user.getString("email");//获取邮箱
        String password = user.getString("password");//获取密码
        String salt = generateSalt();//生成salt
        String uuid = UUID.randomUUID().toString();        //在java中可以利用这个生成唯一的id，用来代替用户的账户名来做检查
        String encryptPassword = encryptPassword(salt, password);//生成加密后的密码
        String[] columns = {"unique_id", "name", "email", "encrypted_password", "salt", "created_at"};
        String[] args = {uuid, name, email,encryptPassword,salt, getCurrentTime()};
        int uid = dbUtils.insert("users", columns, args);//向数据库中写入用户信息
        String[] selectionArgs = {String.valueOf(uid)};
        if(uid != -1) {
            ResultSet rs = dbUtils.query("users", null, "uid = ?", selectionArgs, null, null, null); //查询用户
            try {
                //构造用户的JSONObject格式数据
                if(rs != null && rs.next()) {
                    JSONObject validUser = new JSONObject();
                    validUser.put("name", rs.getString("name"));
                    validUser.put("email", rs.getString("email"));
                    validUser.put("created_at", rs.getString("created_at"));
                    validUser.put("updated_at", rs.getString("updated_at"));
                    validUser.put("uuid", rs.getString("unique_id"));
                    return validUser;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 通过用户的email和密码获取用户信息
     * @param email
     * @param password
     * @return
     */
    public JSONObject getUserByEmailAndPassword(String email, String password) {
        String[] selectionArgs = {email};
        ResultSet rs = dbUtils.query("users", null, "email = ?", selectionArgs, null, null, null);//查询用户
        try {
            if (rs != null && rs.next()) {
                String encryptPassword = rs.getString("encrypted_password");//获取加密后的密码
                String salt = rs.getString("salt");//获取salt
                //判断用户的密码是否匹配,如果匹配,返回用户信息
                if (encryptPassword.equals(encryptPassword(salt, password))) {
                    JSONObject validUser = new JSONObject();
                    validUser.put("name", rs.getString("name"));
                    validUser.put("email", rs.getString("email"));
                    validUser.put("created_at", rs.getString("created_at"));
                    validUser.put("updated_at", rs.getString("updated_at"));
                    validUser.put("uuid", rs.getString("unique_id"));
                    return validUser;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //判断用户是否存在
    public boolean isUserExisted(String email) {
        String[] selectionArgs = {email};
        ResultSet rs = dbUtils.query("users", null, "email = ?", selectionArgs, null, null, null);//查询用户信息
        try {
            //如果存在返回true
            if (rs != null && rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    //生成salt
    public String generateSalt() {
        String salt = String.valueOf(Math.random());
        return salt.substring(0,9);
    }

    //采用SHA-1的加密方式加密密码
    public String encryptPassword(String salt, String password) {         //使用SHA-1加密，并使用base64方式编码

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] encryptPassword = messageDigest.digest((password+salt).getBytes());
            return new BASE64Encoder().encode(encryptPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkPassword(String password,String salt, String encryptPassword) {
        return encryptPassword(salt, password).equals(encryptPassword);
    }

    /**
     * 获取当前时间
     * @return
     */
    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        StringBuilder dateTime = new StringBuilder();
        dateTime.append(year);
        dateTime.append("-");
        dateTime.append(month);
        dateTime.append("-");
        dateTime.append(day);
        dateTime.append(" ");
        dateTime.append(hour);
        dateTime.append(":");
        dateTime.append(minute);
        dateTime.append(":");
        dateTime.append(second);
        return dateTime.toString();
    }


    /**
     * 关闭数据库
     */
    public void closeDB() {
        dbUtils.closeDB();
    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "liwshuo");
        jsonObject.put("email", "email8");
        jsonObject.put("password", "password");
        DBManager dbManager = new DBManager();
        System.out.println(dbManager.getUserByEmailAndPassword("email8", "password"));
    }
}
