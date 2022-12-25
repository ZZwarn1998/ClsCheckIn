package com.dachuang.servlet.login;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import junit.framework.TestCase;

public class TchrRegisterServletTest extends TestCase {
    private final String REQUEST_URL = "http://localhost/TchrRegister";

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testDoPost(){
        try{
            ServletRunner sr = new ServletRunner();
            sr.registerServlet("TchrRegister", TchrRegisterServlet.class.getName());
            ServletUnitClient sc = sr.newClient();
            WebRequest req = new GetMethodWebRequest(REQUEST_URL);
            req.setParameter("log_in_id", "T12133434");
            req.setParameter("log_in_pwd", "a3821345");
            req.setParameter("tchr_name", "stanford");
            req.setParameter("tchr_gen", "M");
            InvocationContext ic = sc.newInvocation(req);
            WebResponse resp = sc.getResponse(req);
            System.out.println(resp.getText().toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}