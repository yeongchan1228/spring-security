package springStudy.springSecurity.config.auth;



import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import springStudy.springSecurity.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * 시큐리티 회원 로그인 설정 1
 *  시큐리티가 /login을 낚아채서 로그인을 진행 시킴 -> 진행 완료되면 session을 만들어줌
 *  시큐리티가 자신만 가지고 있는 세션 -> 키 값으로 구(Security ContextHolder)에 세션 정보를 저장
 *  저장될 오브젝트는 정해짐 => Authentication 타입 객체
 *  Authentication 안에 유저 정보가 있어야 한다.
 *  User 오브젝트의 타입은 => UserDetails 타입 객체
 *
 *  Security Session => 정보를 저장해야 하는데 => 여기에 들어갈 수 있는 객체가 => Authentication 객체 => Authentication 객체 안에 User
 *  정보를 넣어야 하는데 이를 UserDetails이여야 한다.
 *  즉 Session.get -> Authentication -> Authentication.get -> UserDetails(이거로 우리의 User)
 */
@Data
public class PrincipalDetails implements UserDetails, OAuth2User { // UserDetails = PrincipalDetails

    private User user; // 콤 포지
    private Map<String, Object> attributes;

    // 일반 로그인 시 사용
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // Oauth 로그인 시 사용
    // attributes의 내용을 기반으로 User를 생성
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 해당 유저의 권한을 return !

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole().toString();
            }
        });
        return collect;
    }
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 계정이 만료 되지 않았니??

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    // 계정이 잠기지 않았니?

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    // 비밀 번호 유효 기간이 지나지 않았니??

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    // 계정이 활성화 할거니?

    @Override
    public boolean isEnabled() {

        // 우리 사이트에서 회원이 1년 동안 로그인을 안하면? 휴먼 계정으로 전환
        // 현재 시간 - 로그인 시간

        return true;
    }


    //OAuth2User 오버라이드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() { // 사용을 잘 안함
        return attributes.get("sub").toString();
    }
}
