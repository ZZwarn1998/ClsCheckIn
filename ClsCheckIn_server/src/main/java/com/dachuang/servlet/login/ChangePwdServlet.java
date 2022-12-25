package com.dachuang.servlet.login;

import com.dachuang.util.BaseDao;
import com.dachuang.util.CmdRunner;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@WebServlet("/ChangePwd")
public class ChangePwdServlet extends HttpServlet {
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

        Map<String, Object> map = new HashMap<>();

        String msg = "";
        boolean suc = false;

        try{
            if(changePwd(id, pwd)){
                suc = true;
                msg = "Success";
            }else{
                suc = false;
                msg = "Fail to Change PWD";
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        map.put("suc", String.valueOf(suc));
        map.put("msg", msg);
        String json_resp = JSONObject.valueToString(map);
        System.out.println(json_resp);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.write(json_resp);
        pw.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        doGet(request, response);
    }

    protected boolean changePwd(String id, String pwd) throws ClassNotFoundException, IOException, InterruptedException {
       String sql = "UPDATE log_in_tb set log_in_pwd=? where log_in_id=?;";
       String[] params = new String[]{pwd, id};
       BaseDao db = new BaseDao();
       return db.executeUpdateSQL(sql, params);
    }
}
