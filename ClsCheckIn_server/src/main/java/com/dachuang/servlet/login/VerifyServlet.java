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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/Verify")
public class VerifyServlet extends HttpServlet {
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String log_in_id = request.getParameter("log_in_id");
        String msg = "";
        boolean suc = true;
        if (JudgeIdType.judge(log_in_id) != IdType.NONE){
            try{
                if(verify(log_in_id)){
                    suc = true;
                    msg = "Unregistered Account";
                }else{
                    suc = false;
                    msg = "Registered Account";
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            suc = false;
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
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }

    protected boolean verify(String log_in_id) throws ClassNotFoundException {
        String sql = "SELECT log_in_id FROM log_in_tb where log_in_id=?;";
        String[] param = new String[]{log_in_id};
        BaseDao db = new BaseDao();
        List<Map<String, Object>> result = db.executeQuerySQL(sql, param);
        System.out.println(result.toString());
        if(result.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

}
