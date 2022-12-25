package com.dachuang.servlet.tchr_operation;

import com.dachuang.util.BaseDao;
import com.dachuang.util.CmdRunner;
import com.dachuang.util.IdType;
import com.dachuang.util.JudgeIdType;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/TchrUploadAttendance")
public class TchrUploadAttendanceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String file_parent_path = null;
        boolean suc = true;
        String msg = null;
        List<String> attendance = new ArrayList<>();
        try {
            String root = Paths.get(this.getClass().getResource("/").toURI()).getParent().getParent().toString();
            String[] elements = new String[]{root, "txt"};
            file_parent_path = String.join(File.separator, elements);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        System.out.println(file_parent_path);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setRepository(new File(file_parent_path));
        factory.setSizeThreshold(1024 * 1024);
        ServletFileUpload upload = new ServletFileUpload(factory);

        try{
            List<FileItem> fileItems = upload.parseRequest(request);
            for(FileItem item : fileItems){
                String name = item.getFieldName();
                if(item.isFormField()){
                    String val = item.getString();
                    System.out.println("FORM:" + " " + name + " " + val);
                    request.setAttribute(name, val);
                }else {
                    String filename = item.getName();
                    System.out.println("FILE:" + " " + name + " " + filename);
                    request.setAttribute(name, filename);
                    File file = new File(file_parent_path, filename);
                    item.write(file);
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                    BufferedReader br = new BufferedReader(reader);

                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                        if (JudgeIdType.judge(line.trim()).equals(IdType.STU)) {
                            attendance.add(line);
                        } else {
                            suc = false;
                            msg = "Illegal Text File";
                            break;
                        }
                    }
                    reader.close();
                }
            }
            if (suc){

                if(!addNewAttendance(request.getAttribute("id").toString(), request.getAttribute("cls_name").toString(),
                        request.getAttribute("cls_id").toString(), attendance)){
                    suc = false;
                    msg = "Fail to Update Attendance";
                }else{
                    msg = "Success";
                }
            }
        }catch(Exception e){
            e.printStackTrace();
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

    protected boolean addNewAttendance(String id, String cls_name, String cls_id, List<String> attendance){
        BaseDao db = new BaseDao();

        String sql_insert_teaching_tb = "INSERT IGNORE INTO teaching_tb (tchr_id, cls_id) VALUES (?, ?);";
        String[] ttb_params = new String[]{id, cls_id};

        String sql_insert_cls_info_tb = "INSERT IGNORE INTO cls_info_tb (cls_id, cls_name) VALUES (?, ?);";
        String[] citb_params = new String[]{cls_id, cls_name};

        String sql_delete_old_attendance = "DELETE FROM choosing_tb WHERE (cls_id = ?);";
        String[] del_ctb_params = new String[]{cls_id};

        String sql_insert_new_attendance_1 = "INSERT IGNORE INTO choosing_tb (stu_id, cls_id) VALUES ";
        String sql_insert_new_attendance_2 = "";
        List<String> add_ctb_params = new ArrayList<>();
        List<String> items = new ArrayList<>();
        for(String stu_id:attendance){
            String item = "(?, ?)";
            add_ctb_params.add(stu_id);
            add_ctb_params.add(cls_id);
            items.add(item);
        }
        sql_insert_new_attendance_2 = String.join(", ",items) + ";";
        System.out.println(sql_insert_teaching_tb);
        System.out.println(sql_delete_old_attendance);
        String sql_insert_new_attendance = sql_insert_new_attendance_1 + sql_insert_new_attendance_2;
        System.out.println(sql_insert_new_attendance);
        System.out.println(add_ctb_params.toString());
        assert add_ctb_params != null;
        System.out.println(db.executeUpdateSQL(sql_insert_teaching_tb, ttb_params));
        System.out.println(db.executeUpdateSQL(sql_insert_cls_info_tb, citb_params));
        System.out.println(db.executeUpdateSQL(sql_delete_old_attendance, del_ctb_params));
        if(db.executeUpdateSQL(sql_insert_new_attendance, add_ctb_params.toArray())){
            return true;
        }else{
            return false;
        }

    }


    public static DiskFileItemFactory newDiskFileItemFactory(ServletContext context, File repository) {
        //FileCleaningTracker类，这个类用于跟踪要删除的文件
        FileCleaningTracker fileCleaningTracker
                = FileCleanerCleanup.getFileCleaningTracker(context);
        DiskFileItemFactory factory
                = new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD,
                repository);
        factory.setFileCleaningTracker(fileCleaningTracker);
        return factory;
    }
}
