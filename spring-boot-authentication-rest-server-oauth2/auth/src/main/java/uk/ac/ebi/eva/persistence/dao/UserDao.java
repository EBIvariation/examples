package uk.ac.ebi.eva.persistence.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.eva.persistence.entities.User;

@Transactional
public interface UserDao extends CrudRepository<User, String> {
}
