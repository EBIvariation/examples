package uk.ac.ebi.eva.server.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import uk.ac.ebi.eva.server.core.oauth.DatabaseAuthenticationManager;
import uk.ac.ebi.eva.server.core.oauth.DatabaseClientDetailsService;
import uk.ac.ebi.eva.server.core.utils.ConfiguredBCrypt;

public class DatabaseAuthorizationServerConfigurer implements AuthorizationServerConfigurer {

    @Autowired
    private DatabaseClientDetailsService databaseClientDetailsService;

    @Autowired
    private DatabaseAuthenticationManager databaseAuthenticationManager;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(new ConfiguredBCrypt());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(databaseClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(databaseAuthenticationManager);
    }
}
