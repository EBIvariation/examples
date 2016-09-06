package uk.ac.ebi.eva.core.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import uk.ac.ebi.eva.core.oauth.exceptions.AuthenticationNameNull;
import uk.ac.ebi.eva.core.utils.ConfiguredBCrypt;
import uk.ac.ebi.eva.persistence.dao.UserDao;
import uk.ac.ebi.eva.persistence.entities.Group;
import uk.ac.ebi.eva.persistence.entities.Role;
import uk.ac.ebi.eva.persistence.entities.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Service compatible with spring Oauth {@link AuthenticationManager} that checks user and passwords
 * against our database implementation.
 */
@Service
public class DatabaseAuthenticationManager implements AuthenticationManager {

    private static Logger log = LoggerFactory.getLogger(DatabaseAuthenticationManager.class);

    private static final java.lang.String MESSAGE_NAME_NULL = "Authentication name can't be null";
    private static final java.lang.String MESSAGE_INVALID_CREDENTIALS = "Invalid credentials";
    private static final ConfiguredBCrypt bcryp = new ConfiguredBCrypt();

    @Autowired
    private UserDao userDao;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = (String) authentication.getCredentials();
        if (name == null) {
            throw new AuthenticationNameNull(MESSAGE_NAME_NULL);
        }
        User user = userDao.findOne(name);
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException(MESSAGE_INVALID_CREDENTIALS);
        }

        if (!bcryp.matches(password, user.getPassword())) {
            throw new AuthenticationCredentialsNotFoundException(MESSAGE_INVALID_CREDENTIALS);
        }

        // Get roles
        Collection<? extends GrantedAuthority> grantedAuthority = getGrantedAuthority(user);
        log.info("User '" + name + "' authenticated with granted authority '" + grantedAuthority + "'");
        return new UsernamePasswordAuthenticationToken(name, password, grantedAuthority);
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthority(User user) {
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        for (Role role : user.getUserRoles()) {
            grantedAuths.add(new SimpleGrantedAuthority(role.getRoleId()));
        }
        for (Group group: user.getUserGroups()){
            for (Role role : group.getRoles()) {
                grantedAuths.add(new SimpleGrantedAuthority(role.getRoleId()));
            }
        }
        return grantedAuths;
    }

}
