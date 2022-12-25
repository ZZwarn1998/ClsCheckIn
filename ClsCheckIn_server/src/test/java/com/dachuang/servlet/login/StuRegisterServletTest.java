package com.dachuang.servlet.login;

import com.dachuang.util.EncodeImage;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import junit.framework.TestCase;

public class StuRegisterServletTest extends TestCase {
    private final String REQUEST_URL = "http://localhost/StuRegister";

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testDoPost(){
        try{
            ServletRunner sr = new ServletRunner();
            sr.registerServlet("StuRegister", StuRegisterServlet.class.getName());
            ServletUnitClient sc = sr.newClient();
            WebRequest req = new GetMethodWebRequest(REQUEST_URL);
            String face = EncodeImage.cvtImg2Str("D:\\PCproj\\ClsCheckIn_py\\res\\stu\\Mike.jpg");
            req.setParameter("log_in_id", "s");
            req.setParameter("log_in_pwd", "test");
            req.setParameter("s_name", "test");
            req.setParameter("s_gen", "M");
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