package springStudy.jwt.filter.jwt;

public interface JwtProperties {

    String SECRET = "cos";
    int EXPIRATION_TIME = (60000) * 6; // 6분
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
