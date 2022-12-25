package com.dachuang.servlet.tchr_operation;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import junit.framework.TestCase;

import java.io.InputStream;

public class TchrCheckAttendanceServletTest extends TestCase {
    private final String REQUEST_URL = "http://localhost/TchrCheckAttendance";

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testDoPost(){
        try{
            ServletRunner sr = new ServletRunner();
            sr.registerServlet("TchrCheckAttendance", TchrCheckAttendanceServlet.class.getName());
            ServletUnitClient sc = sr.newClient();
            WebRequest req = new GetMethodWebRequest(REQUEST_URL);
            req.setParameter("t_id", "T12130000");
            req.setParameter("c_id", "cse102");
            InvocationContext ic = sc.newInvocation(req);
//            System.out.println(req.toString());
            WebResponse resp = sc.getResponse(req);

            System.out.println(resp.getText());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}