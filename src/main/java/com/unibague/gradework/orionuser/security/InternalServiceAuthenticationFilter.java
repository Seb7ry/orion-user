package com.unibague.gradework.orionuser.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class InternalServiceAuthenticationFilter extends OncePerRequestFilter {

  private final ServiceAuthProperties props;

  public InternalServiceAuthenticationFilter(ServiceAuthProperties props) {
    this.props = props;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    try {
      if (props.isAllowInternalHeader() && SecurityContextHolder.getContext().getAuthentication() == null) {

        String xInternal = header(req, "X-Internal-Request");
        String xSvcReq   = header(req, "X-Service-Request");
        String xSvcName  = header(req, "X-Service-Name");
        String xSvcToken = header(req, "X-Service-Token");

        boolean isInternal = "true".equalsIgnoreCase(xInternal) && "true".equalsIgnoreCase(xSvcReq);
        boolean hasToken   = xSvcToken != null && !xSvcToken.isBlank();
        boolean tokenOk    = isInternal && hasToken && xSvcToken.equals(props.getToken());

        if (tokenOk) {
          var authorities = List.of(new SimpleGrantedAuthority(props.getAdminRole()));
          var auth = new UsernamePasswordAuthenticationToken("system", "N/A", authorities);
          SecurityContextHolder.getContext().setAuthentication(auth);
          // (opcional) log: System.out.println("Injected SYSTEM user for internal request: " + xSvcName);
        }
      }
    } catch (Exception ex) {
      // nunca tumbes la request por el filtro; sigue y deja que la seguridad normal responda 401/403 si aplica.
      // (opcional) log: ex.printStackTrace();
    }

    chain.doFilter(req, res);
  }

  private String header(HttpServletRequest req, String name) {
    String v = req.getHeader(name);
    return v == null ? null : v.trim();
  }
}
