package com.dachuang.servlet.tchr_operation;

import com.dachuang.util.BaseDao;
import com.dachuang.util.IdType;
import com.dachuang.util.JudgeIdType;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.json.JSONObject;

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

@WebServlet("/TchrUpdate")
public class TchrUpdateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
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
        String tchr_id = param2val.get("t_id");
        String tchr_name = param2val.get("t_name");
        String tchr_gen = param2val.get("t_gen");

        int flag = 0;
        boolean suc = false;
        String msg = "";

        try{
            flag = update(tchr_id, tchr_name, tchr_gen);
        }catch (Exception e){
            e.printStackTrace();
        }

        switch (flag){
            case 0:
                suc = true;
                msg = "Success";
                break;
            case 1:
                msg = "Unregistered account";
                break;
            default:
                break;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("suc", String.valueOf(suc));
        map.put("msg", msg);
        String json_resp = JSONObject.valueToString(map);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.write(json_resp);
        pw.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        doGet(request, response);
    }

    protected int update(String id, String name, String gen) throws SQLException, IOException, ClassNotFoundException {
        String sql = "SELECT log_in_pwd FROM log_in_tb WHERE log_in_id=?;";
        BaseDao db = new BaseDao();
        String[] param = new String[]{id};
        List<Map<String, Object>> result = db.executeQuerySQL(sql, param);
        if(!result.isEmpty()){  // Registered account
            sql = "UPDATE tchr_info_tb SET tchr_id=?, tchr_name=?, gender=? WHERE (tchr_id=?);";
            param = new String[]{id, name, gen, id};
            System.out.println(param.toString());
            db.executeUpdateSQL(sql, param);
            return 0;
        }
        return 1;
    }

}
