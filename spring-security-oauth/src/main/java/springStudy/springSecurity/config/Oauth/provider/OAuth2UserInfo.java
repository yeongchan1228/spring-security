package springStudy.springSecurity.config.Oauth.provider;

public interface OAuth2UserInfo {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();

}
