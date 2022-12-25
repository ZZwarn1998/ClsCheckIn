package com.dachuang.servlet.login;

import com.dachuang.util.BaseDao;
import com.dachuang.util.IdType;
import com.dachuang.util.JudgeIdType;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/LogIn")
public class LogInServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        StringBuilder sb = new StringBuilder();
        InputStream is = request.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "GBK"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        reader.close();
        System.out.println(sb);

        String[] equations = URLDecoder.decode(sb.toString(), "UTF-8").split("&");
        Map<String, String> param2val = new HashMap<>();
        for(String equ : equations){
            String[] pairs = equ.split("=");
            param2val.put(pairs[0], pairs[1]);
        }
        String id = param2val.get("log_in_id");
        String pwd = param2val.get("log_in_pwd");

        int flag = 0;

        try{
            flag = logIn(id, pwd);
        }catch (Exception e){
            e.printStackTrace();
        }

        boolean suc = false;
        String msg = "";
        String name = null;
        IdType idt = JudgeIdType.judge(id);
        switch (flag){
            case 0:
                suc = true;
                msg = "Success";
                try {
                    name = getName(id);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                msg = "Unregistered Account";
                break;
            case 2:
                msg = "Wrong PWD";
                break;
            default:
                break;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("suc", String.valueOf(suc));
        map.put("msg", msg);
        map.put("name", name);
        String json_resp = JSONObject.valueToString(map);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.write(String.valueOf(json_resp));
        pw.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        doGet(request, response);
    }

    protected int logIn(String id, String pwd) throws ClassNotFoundException, SQLException {
        String sql = "SELECT log_in_pwd FROM log_in_tb where log_in_id=?;";
        String[] param = new String[]{id};
        BaseDao db = new BaseDao();
        List<Map<String, Object>> result = db.executeQuerySQL(sql, param);
        if(result.isEmpty()){
            return 1;
        }else {
            String real_pwd = result.get(0).get("log_in_pwd").toString();
            if (real_pwd.equals(pwd)) {
                return 0;
            } else {
                return 2;
            }
        }
    }

    protected String getName(String id) throws ClassNotFoundException {
        if(JudgeIdType.judge(id).equals(IdType.STU)){
            String sql = "SELECT stu_name FROM stu_info_tb where stu_id=?;";
            String[] params = new String[]{id};
            BaseDao db = new BaseDao();
            List<Map<String, Object>> result = db.executeQuerySQL(sql, params);
            System.out.println(result.toString());
            if(result.isEmpty()){
                return null;
            }else{
                return result.get(0).get("stu_name").toString();
            }
        }else if (JudgeIdType.judge(id).equals(IdType.TCHR)){
            String sql = "SELECT tchr_name FROM tchr_info_tb where tchr_id=?;";
            String[] params = new String[]{id};
            BaseDao db = new BaseDao();
            List<Map<String, Object>> result = db.executeQuerySQL(sql, params);
            System.out.println(result.toString());
            if(result.isEmpty()){
                return null;
            }else{
                return result.get(0).get("tchr_name").toString();
            }
        }else{
            return null;
        }
    }
}
