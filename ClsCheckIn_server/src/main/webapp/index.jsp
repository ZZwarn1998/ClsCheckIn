<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.io.File" %>
<%@ page import="com.dachuang.util.DecodeImage" %>
<%@ page import="java.net.URISyntaxException" %>
<%@ page import="com.sun.xml.internal.bind.v2.model.annotation.FieldLocatable" %>
<%@page contentType="text/html;charset=UTF-8" language="java"  %>
<html>
<body>
<h2>Welcome!</h2>
    <%
        try{
            String root = Paths.get(this.getClass().getResource("/").toURI()).getParent().getParent().toString();
            String stu_face_path = root + File.separator + "img" + File.separator + "stu_face";
            String attend_path = root + File.separator + "img" + File.separator + "attend";
            String result_path = root + File.separator + "txt" + File.separator + "result";
            File stu_face_folder = new File(stu_face_path);
            File attend_folder = new File(attend_path);
            File result_folder = new File(result_path);
            System.out.println(stu_face_path);
            System.out.println(attend_path);
            if (!stu_face_folder.exists()){
                boolean result_rec = stu_face_folder.mkdirs();
            }
            if (!stu_face_folder.exists()) {
                boolean result_rec = stu_face_folder.mkdirs();
            }
            if (!result_folder.exists()){
                boolean result_rec = result_folder.mkdirs();
            }
        }catch(URISyntaxException e){
            e.printStackTrace();
        }

    %>
</body>
</html>
