package com.dachuang.servlet.login;

import com.meterware.httpunit.GetMethodWebRequest;

import com.meterware.httpunit.WebRequest;

import com.meterware.httpunit.WebResponse;


import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.Assert;

import javax.xml.ws.WebEndpoint;

public class ChangePwdServletTest extends TestCase{

    private final String REQUEST_URL = "http://localhost/ChangePwd";

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testDoPost(){
        try{
            ServletRunner sr = new ServletRunner();
            sr.registerServlet("ChangePwd", ChangePwdServlet.class.getName());
            ServletUnitClient sc = sr.newClient();
            WebRequest req = new GetMethodWebRequest(REQUEST_URL);
            req.setParameter("log_in_id", "t12139000");
            req.setParameter("new_log_in_pwd", "new");
            req.setParameter("name", "ZZC");
            InvocationContext ic = sc.newInvocation(req);
//            ChangePwdServlet is = (ChangePwdServlet) ic.getServlet();
            WebResponse resp = sc.getResponse(req);
//            Assert.assertTrue(is.changePwd("", "",""));
            System.out.println(resp.getText().toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}