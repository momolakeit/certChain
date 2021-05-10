package com.momo.certChain.services.request;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.momo.certChain.jwt.JwtProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@Service
@Scope(value= WebApplicationContext.SCOPE_REQUEST,proxyMode= ScopedProxyMode.TARGET_CLASS)
public class HeaderCatcherService {

    private HttpServletRequest httpServletRequest;

    private JwtProvider jwtProvider;

    private final String AUTHORIZATION_HEADER="Authorization";

    public HeaderCatcherService(HttpServletRequest httpServletRequest, JwtProvider jwtProvider) {
        this.httpServletRequest = httpServletRequest;
        this.jwtProvider = jwtProvider;
    }

    public String getUserId(){
        DecodedJWT decodedJWT = jwtProvider.verify(httpServletRequest.getHeader(AUTHORIZATION_HEADER));
        return decodedJWT.getSubject();
    }
}
