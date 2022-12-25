package com.dachuang.servlet.tchr_operation;

import com.dachuang.util.CmdRunner;
import com.dachuang.util.DecodeImage;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.Buffer;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@WebServlet("/TchrCheckAttendance")
public class TchrCheckAttendanceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        StringBuilder sb = new StringBuilder();
        InputStream is = request.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "GBK"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        reader.close();

        String[] equations = URLDecoder.decode(sb.toString(), "UTF-8").split("&");
        Map<String, String> param2val = new HashMap<>();
        for(String equ : equations){
            String[] pairs = equ.split("=");
            param2val.put(pairs[0], pairs[1]);
        }

        String cls_id = param2val.get("cls_id");
        String tchr_id = param2val.get("t_id");
        String gp_pic = param2val.get("gp_pic");

        String root = null;
        try {
            root = Paths.get(this.getClass().getResource("/").toURI()).getParent().getParent().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String txt_name = cls_id + "_" + tchr_id + ".txt";
        String pic_name = cls_id + "_" + tchr_id + ".jpg";
        String save_path = root + File.separator + "txt" + File.separator + "result" + File.separator + txt_name;
        String gp_pic_path = root + File.separator + "img" +  File.separator + pic_name;

        DecodeImage.cvtStr2Img(gp_pic, gp_pic_path);

        String msg = "Success";
        boolean suc = true;
        String result = "";

        try{
            String[] cmd = new String[]{CmdRunner.get_conda_bat(), "activate", "cls", "&&",
                    CmdRunner.get_py_interpreter_loc(), CmdRunner.get_py_cmd_loc(), "find_absent_stu", "--img_path", gp_pic_path, "--cls_id", cls_id, "--save_path", save_path};
            CmdRunner.runCommand(cmd);
            File file = new File(save_path);
            if(file.exists()){
                InputStreamReader fileReader = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String item = null;
                while((item = bufferedReader.readLine()) != null){
                    result = result + item;
               }
               fileReader.close();
            }
        }catch (Exception e){
            suc = false;
            msg = "Fail to Get Result";
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();
        map.put("suc", String.valueOf(suc));
        map.put("msg", msg);
        map.put("result", result);
        System.out.println(result);
        String json_resp = JSONObject.valueToString(map);
        PrintWriter pw = response.getWriter();
        pw.write(json_resp);
        pw.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        doGet(request, response);
    }


}
