package springStudy.jwt.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.UTF8DataInputJsonParser;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * header의 Authorization 확인 필터는 시큐리티 필터가 돌기 전에 돌아야 한다.
 * 따라서 우리는 토큰을 만들어 줘야 하는데 정상적으로 id, pw가 들어와서 로그인이 되면 그때 생성해서 응답.
 * 이 후 사용자는 요청할 때마다 header에 Authorization에 value 값으로 넣어줘야 한다.
 * 서버는 넘어온 토큰이 우리가 만든 토큰와 같은지 확인(RSA, SH256)
 * RSA -> 개인키로 잠궈서 보내고 오면 공개키로 확인
 * SH256 -> 개인키로 잠궈서 온 값을 다시 잠궈서 시그니쳐와 확인
 */
public class JwtTemporaryTokenFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 토큰을 만들었다고 가정하면 이렇게 토큰을 받으면 된다. -> 우리가 원하는 토큰이 있는 요청만 처리
        if(req.getMethod().equals("POST")) {
            String headAuthorization = req.getHeader("Authorization");
            System.out.println("headAuthorization = " + headAuthorization);

            // 우리가 원하는 토큰을 가지고 있는 사람만 통과
            if(headAuthorization.equals("hello")){
                chain.doFilter(request, response);
            }else{
                PrintWriter out = res.getWriter();
                out.println("인증 안됨");
            }
        }
    }
}
