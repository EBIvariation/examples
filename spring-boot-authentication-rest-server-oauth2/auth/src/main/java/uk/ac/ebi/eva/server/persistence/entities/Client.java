package uk.ac.ebi.eva.server.persistence.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This entity defines a client as specified in spring oauth2 configuration.
 */
@Entity
@Table(name ="CLIENTS")
public class Client {

    @Id
    @Column(length = 256)
    private String clientId;

    @Column(length = 256, nullable = false)
    private String clientSecret;

    @Column(columnDefinition="tinyint(1) default 1")
    private boolean secretRequired;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CLIENT_AUTHORIZED_GRANTS")
    private final Set<String> authorizedGrantTypes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CLIENT_SCOPES")
    private final Set<String> scope;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CLIENT_REGISTERED_REDIRECT_URIS")
    private final Set<String> registeredRedirectUri;

    private Integer accessTokenValiditySeconds;

    private Integer refreshTokenValiditySeconds;



    public Client(){
        secretRequired = true;
        authorizedGrantTypes=new HashSet<>();
        scope = new HashSet<>();
        registeredRedirectUri = new HashSet<>();
    }

    /**
     * Returns the unique client identifier in the server
     * @return
     */
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Returns the client secret to stablish communications between client and
     * server. This works as a password and should be stored encrypted.
     * @return
     */
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * It's supported by the standard, but not desirable
     * @return
     */
    public boolean isSecretRequired() {
        return secretRequired;
    }

    public void setSecretRequired(boolean secretRequired) {
        this.secretRequired = secretRequired;
    }

    /**
     * Returns all the authorized grant types that are enabled for this client.
     * The basic grant types as stated in oauth are:
     *  - authorization_code
     *  - refresh_token
     *  - password
     * @return
     */
    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    /**
     * Returns the scopes where this client is authorized in the resource server.
     * @return
     */
    public Set<String> getScope() {
        return scope;
    }

    /**
     * Returns the list of urls where the authorization flow can redirect after
     * a successful authorization.
     * @return
     */
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }
}
