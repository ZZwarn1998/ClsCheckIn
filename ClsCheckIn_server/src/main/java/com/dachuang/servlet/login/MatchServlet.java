package com.dachuang.servlet.login;

import com.dachuang.util.BaseDao;
import com.dachuang.util.IdType;
import com.dachuang.util.JudgeIdType;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/Match")
public class MatchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        String id = param2val.get("id");
        String name = param2val.get("name");
        String sql = "";
        boolean suc = false;
        String msg = "";

        if(JudgeIdType.judge(id).equals(IdType.STU)){
            sql = "SELECT * FROM stu_info_tb where stu_id=? and stu_name=?;";
            try {
                suc = ifMatch(id, name, sql);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (suc){
                msg = "Match";
            }else{
                msg = "Not Match";
            }
        }else if (JudgeIdType.judge(id).equals(IdType.TCHR)){
            sql = "SELECT * FROM tchr_info_tb where tchr_id=? and tchr_name=?;";
            try {
                suc = ifMatch(id, name, sql);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (suc){
                msg = "Match";
            }else{
                msg = "Not Match";
            }
        }else{
            msg = "Illegal ID";
        }

        Map<String, Object> map = new HashMap<>();
        map.put("suc", String.valueOf(suc));
        map.put("msg", msg);
        System.out.println(msg);
        String json_resp = JSONObject.valueToString(map);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.write(json_resp);
        pw.flush();

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected boolean ifMatch(String id, String name, String sql) throws ClassNotFoundException {
        String[] param = new String[]{id, name};
        BaseDao db = new BaseDao();
        List<Map<String, Object>> result = db.executeQuerySQL(sql, param);
        System.out.println(result.toString());
        if(result.isEmpty()){
            return false;
        }else{
            return true;
        }
    }
}
