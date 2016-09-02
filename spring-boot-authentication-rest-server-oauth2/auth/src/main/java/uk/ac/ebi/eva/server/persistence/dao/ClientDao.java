package uk.ac.ebi.eva.server.persistence.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.eva.server.persistence.entities.Client;

@Transactional
public interface ClientDao extends CrudRepository<Client, String> {

}
