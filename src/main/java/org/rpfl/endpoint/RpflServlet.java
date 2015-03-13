package org.rpfl.endpoint;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.rpfl.assembly.common.RequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

@Singleton
public class RpflServlet extends HttpServlet {

    @Inject
    private RequestHandler requestHandler;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            requestHandler.handle(request, response);
        } catch (SignatureException e) {
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(SC_METHOD_NOT_ALLOWED);
    }
}
