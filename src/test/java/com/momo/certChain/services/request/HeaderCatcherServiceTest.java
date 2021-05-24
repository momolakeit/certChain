package com.momo.certChain.services.request;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.momo.certChain.jwt.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeaderCatcherServiceTest {
    @InjectMocks
    private HeaderCatcherService headerCatcherService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private HttpServletRequest httpServletRequest;

    private final String AUTHORIZATION_HEADER="Authorization";

    private final String id = "123456";

    @Test
    public void testGetUserId(){
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn("jwt token");
        when(jwtProvider.verify(anyString())).thenReturn(createDecodedJWT());

        assertEquals(id,headerCatcherService.getUserId());
    }

    private DecodedJWT createDecodedJWT(){
        return  new DecodedJWT() {
            @Override
            public String getToken() {
                return null;
            }

            @Override
            public String getHeader() {
                return null;
            }

            @Override
            public String getPayload() {
                return null;
            }

            @Override
            public String getSignature() {
                return null;
            }

            @Override
            public String getAlgorithm() {
                return null;
            }

            @Override
            public String getType() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public String getKeyId() {
                return null;
            }

            @Override
            public Claim getHeaderClaim(String s) {
                return null;
            }

            @Override
            public String getIssuer() {
                return null;
            }

            @Override
            public String getSubject() {
                return id;
            }

            @Override
            public List<String> getAudience() {
                return null;
            }

            @Override
            public Date getExpiresAt() {
                return null;
            }

            @Override
            public Date getNotBefore() {
                return null;
            }

            @Override
            public Date getIssuedAt() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public Claim getClaim(String s) {
                return null;
            }

            @Override
            public Map<String, Claim> getClaims() {
                return null;
            }
        };
    }


}