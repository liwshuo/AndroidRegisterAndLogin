package com.bupt.shuo.registandlogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bupt.shuo.registandlogin.helper.AppConfig;
import com.bupt.shuo.registandlogin.helper.AppController;
import com.bupt.shuo.registandlogin.helper.SQLiteManager;
import com.bupt.shuo.registandlogin.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户注册页面
 */
public class RegisterActivity extends ActionBarActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button registerButton;
    private Button linkToLoginButton;
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = (EditText) findViewById(R.id.nameInput);//姓名输入框
        emailInput = (EditText) findViewById(R.id.emailInput);//邮箱输入框
        passwordInput = (EditText) findViewById(R.id.passwordInput);//密码输入框
        registerButton = (Button) findViewById(R.id.registerButton);//注册按钮
        linkToLoginButton = (Button) findViewById(R.id.linkToLoginScreenButton);//跳转到登陆页面按钮
        progressDialog = new ProgressDialog(this);//进度条
        progressDialog.setCancelable(false);
        sessionManager = new SessionManager(getApplicationContext());
        //如果已经登陆,那么跳转到用户信息页面
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(RegisterActivity.this, UserDetailActivity.class);
            startActivity(intent);
            finish();
        }

        //注册按钮点击操作
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                boolean cancel = false;
                View focusView = null;
                String errorMsg = null;
                //判断密码是否为空
                if(TextUtils.isEmpty(password)){
                    focusView = passwordInput;
                    cancel = true;
                    errorMsg = "请输入密码哦";
              //      passwordInput.setError("请输入密码哦");
                }else if(!isPasswordValid(password)) {
                    focusView = passwordInput;
                    cancel = true;
                    errorMsg = "密码长度不能少于6位哦";
          //          passwordInput.setError("密码长度不能少于6位哦");
                }
                //判断名字是否为空
                if (TextUtils.isEmpty(name)) {
                    focusView = nameInput;
                    cancel = true;
                    errorMsg = "请输入名字哦";
            //        nameInput.setError("请输入名字哦");
                }

                //判断邮箱是否为空且是否符合格式,必须包含@
                if (TextUtils.isEmpty(email)) {
                    focusView = emailInput;
                    cancel = true;
                    errorMsg = "请输入邮箱哦";
             //       emailInput.setError("请输入邮箱哦");
                }else if (!isEmailValid(email)) {
                    focusView = emailInput;
                    cancel = true;
                    errorMsg = "邮箱格式不对哦";
                //    emailInput.setError("邮箱格式不对哦");
                }

                //判断用户输入是否有效,如果有效,则进行注册操作
                if (!cancel) {
                    registerUser(name, email, password);
                }else {
                    focusView.requestFocus();
                    cancel = false;
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        });

        //跳转到登陆页面操作
        linkToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("click");
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //注册用户
    private void registerUser(final String name, final String email, final String password) {
        String tag_string_req = "req_register";

        progressDialog.setMessage("注册中...");
        showDialog();
        //注册请求
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER_Test,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    boolean error = jsonObject.getBoolean("error");
                    //注册成功则跳转到登陆页面,否则显示错误信息
                    if (!error) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        hideDialog();
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideDialog();
                Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_LONG).show();
            }
        }) {
            //注册的post参数
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };
        //添加request到queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }

    //显示进度条
    private void showDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    //隐藏进度条
    private void hideDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    //判断邮箱是否包含@
    private boolean isEmailValid(String email) {
        if(!email.contains("@")) {
            return false;
        }else {
            return true;
        }
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            return false;
        }else {
            return true;
        }
    }
}