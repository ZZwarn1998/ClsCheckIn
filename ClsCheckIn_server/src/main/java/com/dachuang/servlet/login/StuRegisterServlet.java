package com.dachuang.servlet.login;

import com.dachuang.util.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;
import org.omg.PortableInterceptor.SUCCESSFUL;
import sun.awt.datatransfer.DataTransferer;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@WebServlet("/StuRegister")
public class StuRegisterServlet extends HttpServlet {
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
        String s_id = param2val.get("log_in_id");
        String s_pwd = param2val.get("log_in_pwd");
        String s_face = param2val.get("s_face");
        String s_name = param2val.get("s_name");
        String s_gen = param2val.get("s_gen");

        System.out.println(s_face.substring(0,38));
        System.out.println(s_pwd);
        System.out.println(s_name);
        System.out.println(s_id);
        System.out.println(s_gen);

        String s_face_path = getFaceImgPath(s_id, s_name);
        DecodeImage.cvtStr2Img(s_face, s_face_path);
        System.out.println(s_face_path);

        int flag = 0;
        boolean suc = false;
        String msg = "";

        try{
            flag = register(s_id, s_pwd, s_name, s_gen, s_face_path);
        }catch (Exception e){
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

        File stu_face_img = new File(s_face_path);
        if(stu_face_img.delete()){
            System.out.println("Successfully delete " + s_face_path);
        }else{
            System.out.println("Fail to delete " + s_face_path);
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
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }

    protected String getFaceImgPath(String id, String name){
        String face_path = null;
        try {
            String root = Paths.get(this.getClass().getResource("/").toURI()).getParent().getParent().toString();
            root = "D:\\Tomcat\\apache-tomcat-8.5.82\\webapps\\ClsCheckIn";  // test
            String pic_name = name + "_" + id + ".jpg";
            String[] elements = new String[]{root, "img", "stu_face", pic_name};
            face_path = String.join(File.separator, elements);
        }catch (Exception e){
            e.printStackTrace();
        }
        return face_path;
    }

    protected int register(String id, String pwd, String name, String gen, String face_path) throws ClassNotFoundException, InterruptedException, IOException, URISyntaxException {
        String sql = "SELECT log_in_pwd FROM log_in_tb where log_in_id=?;";
        String[] param = new String[]{id};
        BaseDao db = new BaseDao();
        List<Map<String, Object>> result = db.executeQuerySQL(sql, param);
        if(result.isEmpty()){ // Unregistered account
            sql = "INSERT INTO log_in_tb (log_in_id, log_in_pwd) " +
                    "VALUES (?, ?);";
            param = new String[]{id, pwd};
            db.executeUpdateSQL(sql, param);

            String[] cmd = new String[]{CmdRunner.get_conda_bat(), "activate", "cls", "&&",
                    CmdRunner.get_py_interpreter_loc(), CmdRunner.get_py_cmd_loc(), "update_stu", "--s_name", name, "--s_id", id, "--s_gen", gen, "--s_face_path", face_path};
            CmdRunner.runCommand(cmd);
            return 0;
        }
        return 1;
    }

}
