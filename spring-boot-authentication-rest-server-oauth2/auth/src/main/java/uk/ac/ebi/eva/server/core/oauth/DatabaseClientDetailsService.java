package uk.ac.ebi.eva.server.core.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import uk.ac.ebi.eva.server.core.utils.DatabaseClientDetailsAdapter;
import uk.ac.ebi.eva.server.persistence.dao.ClientDao;
import uk.ac.ebi.eva.server.persistence.entities.Client;

/**
 * Service compatible with spring Oauth {@link ClientDetailsService} that checks client against
 * our database implementation.
 */
@Service
public class DatabaseClientDetailsService implements ClientDetailsService{

    private static Logger log = LoggerFactory.getLogger(DatabaseClientDetailsService.class);

    @Autowired
    private ClientDao clientDao;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        if(clientId == null){
            log.info("clientId '"+clientId+"' is invalid");
            return null;
        }
        Client client = clientDao.findOne(clientId);
        if(client == null){
            log.info("clientId '"+clientId+"' doesn't exist");
            return null;
        }

        return new DatabaseClientDetailsAdapter(client);
    }
}
