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
import java.util.Locale;
import java.util.Map;

@WebServlet("/TchrRegister")
public class TchrRegisterServlet extends HttpServlet {
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
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
        String t_id = param2val.get("log_in_id");
        String t_pwd = param2val.get("log_in_pwd");
        String t_name = param2val.get("t_name");
        String t_gen = param2val.get("t_gen");

        boolean suc = false;
        String msg = "";
        int flag = 0;

        try{
           flag = register(t_id, t_pwd, t_name, t_gen);
        }catch (Exception e) {
            e.printStackTrace();
        }
        switch (flag){
            case 0:
                suc = true;
                msg = "Welcome";
                break;
            case 1:
                msg = "Fail to Register";
                break;
            default:
                break;
        }
        Map<String, String> map = new HashMap<>();
        map.put("suc", String.valueOf(suc));
        map.put("msg", msg);
        String json_resp = JSONObject.valueToString(map);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.write(json_resp);
        pw.flush();

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        doGet(request, response);
    }

    protected int register(String id, String pwd, String name, String gen) throws IOException, SQLException, ClassNotFoundException {
        String sql = "SELECT log_in_pwd FROM log_in_tb where log_in_id=?;";
        String[] param = new String[]{id};
        BaseDao db = new BaseDao();
        List<Map<String, Object>> result = db.executeQuerySQL(sql, param);
        if(result.isEmpty()){ // Unregistered account
            sql = "INSERT INTO log_in_tb (log_in_id, log_in_pwd) " +
                    "VALUES (?, ?);";
            param = new String[]{id, pwd};
            db.executeUpdateSQL(sql, param);

            sql = "INSERT INTO tchr_info_tb (tchr_id, tchr_name, gender) " +
                    "VALUES (?, ?, ?);";
            param = new String[]{id, name, gen};
            db.executeUpdateSQL(sql, param);
            return 0;
        }
        return 1;
    }

}
