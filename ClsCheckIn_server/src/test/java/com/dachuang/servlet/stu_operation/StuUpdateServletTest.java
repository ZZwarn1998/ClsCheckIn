package com.dachuang.servlet.stu_operation;

import com.dachuang.servlet.login.StuRegisterServlet;
import com.dachuang.util.EncodeImage;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import junit.framework.TestCase;

public class StuUpdateServletTest extends TestCase {
    private final String REQUEST_URL = "http://localhost/StuUpdate";

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testDoPost(){
        try{
            ServletRunner sr = new ServletRunner();
            sr.registerServlet("StuUpdate", StuUpdateServlet.class.getName());
            ServletUnitClient sc = sr.newClient();
            WebRequest req = new GetMethodWebRequest(REQUEST_URL);
            String face = EncodeImage.cvtImg2Str("D:\\PCproj\\ClsCheckIn_py\\res\\stu\\Mike.jpg");
            req.setParameter("s_id", "12137778");
            req.setParameter("s_name", "TEST_NEW");
            req.setParameter("s_gen", "F");
            req.setParameter("s_face", face);
            InvocationContext ic = sc.newInvocation(req);
//            System.out.println(req.toString());
            WebResponse resp = sc.getResponse(req);
            System.out.println(resp.getText());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}