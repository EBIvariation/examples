package uk.ac.ebi.eva.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.eva.persistence.entities.Client;
import uk.ac.ebi.eva.persistence.entities.PollAnswers;
import uk.ac.ebi.eva.persistence.repository.CountGroupByRepository;

/**
 * Created by jorizci on 29/09/16.
 */
public interface PollAnswersDao extends JpaRepository<PollAnswers, Long>, CountGroupByRepository<PollAnswers, Long> {
}
