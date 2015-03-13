package org.rpfl.assembly.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;

public interface RequestHandler {
    public void handle(HttpServletRequest var1, HttpServletResponse var2) throws IOException, SignatureException;
}
