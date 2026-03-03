package org.magikarp.marketscanner.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class GoogleEmailOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = super.loadUser(userRequest);
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(new OAuth2Error(
                    "missing_email",
                    "Google account did not provide an email address.",
                    null
            ));
        }
        return new DefaultOidcUser(
                user.getAuthorities().isEmpty()
                        ? java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        : user.getAuthorities(),
                user.getIdToken(),
                user.getUserInfo(),
                "email"
        );
    }
}
