package com.dachuang.servlet.tchr_operation;

import com.dachuang.util.BaseDao;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/ReFreshSpinner")
public class ReFreshSpinnerServlet extends HttpServlet {
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
        String tchr_id = param2val.get("t_id");
        boolean suc = false;
        String msg = "";
        Map<String, String> cls_id2cls_name = new HashMap<>();
        String json_result = "";

        String sql = "SELECT cls_info_tb.cls_id, cls_info_tb.cls_name from teaching_tb inner join cls_info_tb on teaching_tb.cls_id = cls_info_tb.cls_id where teaching_tb.tchr_id=?;";
        String[] params = new String[]{tchr_id};
        BaseDao db = new BaseDao();
        List<Map<String, Object>> result = null;

        try {
            result = db.executeQuerySQL(sql, params);
            if (result.isEmpty()){
                msg = "No Course";
            }else{
                suc = true;
                msg = "Success";
                for(Map<String, Object> item : result){
                    cls_id2cls_name.put(String.valueOf(item.get("cls_id")), String.valueOf(item.get("cls_name")));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        json_result = JSONObject.valueToString(cls_id2cls_name);
        Map<String, String> map = new HashMap<>();
        map.put("suc", String.valueOf(suc));
        map.put("msg", msg);
        map.put("result", json_result);
        System.out.println(json_result);
        String json_resp = JSONObject.valueToString(map);
        PrintWriter pw = response.getWriter();
        pw.write(json_resp);
        pw.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
