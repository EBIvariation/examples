package uk.ac.ebi.eva;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.data.jpa.domain.Specifications.where;
import static uk.ac.ebi.eva.persistence.specifications.GenericSpecifications.isEqual;

import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.eva.persistence.dao.PollAnswersDao;
import uk.ac.ebi.eva.persistence.entities.PollAnswers;
import uk.ac.ebi.eva.persistence.repository.CountGroupByRepositoryImpl;

import javax.persistence.Tuple;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = PollAnswersDao.class)
@EnableJpaRepositories(basePackages = {"uk.ac.ebi.eva.persistence.dao","uk.ac.ebi.eva.persistence.repository"}, repositoryBaseClass = CountGroupByRepositoryImpl.class)
public class CustomRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomRepositoryTest.class);
    private static final String POLL_NAME_A = "poll_name_a";
    private static final String POLL_NAME_B = "poll_name_b";
    private static final long ANSWER_LONG_10 = 10l;
    private static final String ANSWER_KIWI = "kiwi";
    private static final String ANSWER_APPLE = "apple";
    private static final long ID_ONE = -1L;
    private static final long ID_TWO = -2L;
    private static final long ID_THREE = -3L;

    @Autowired
    private PollAnswersDao pollAnswersDao;

    private void storeAnswerDao(long id, String pollName, long answerLong, boolean answerBoolean, String answerText ){

        PollAnswers pollAnswers = new PollAnswers();
        pollAnswers.setId(id);
        pollAnswers.setPollName(pollName);
        pollAnswers.setAnswerLong(answerLong);
        pollAnswers.setAnswerBoolean(answerBoolean);
        pollAnswers.setAnswerText(answerText);

        long count = pollAnswersDao.count();
        pollAnswersDao.save(pollAnswers);
        Assert.assertEquals(count + 1, pollAnswersDao.count());

    }

    @Test
    public void storeTest() {
        storeAnswerDao(ID_ONE, POLL_NAME_A, ANSWER_LONG_10, true, ANSWER_KIWI);
    }

    @Test
    public void readTest() {
        storeAnswerDao(ID_ONE, POLL_NAME_A, ANSWER_LONG_10, true, ANSWER_KIWI);
        pollAnswersDao.getOne(ID_ONE);
    }

    @Test
    public void simpleCountTest() {
        List<Tuple> groupCount;
        storeAnswerDao(ID_ONE, POLL_NAME_A, ANSWER_LONG_10, true, ANSWER_KIWI);
        storeAnswerDao(ID_TWO, POLL_NAME_B, ANSWER_LONG_10, true, ANSWER_KIWI);
        storeAnswerDao(ID_THREE, POLL_NAME_B, ANSWER_LONG_10, false, ANSWER_APPLE);

        groupCount = pollAnswersDao.groupCount("pollName");
        Assert.assertEquals(2,groupCount.size());
        Assert.assertEquals(1L,(long)groupCount.get(0).get(1));
        Assert.assertEquals(2L,(long)groupCount.get(1).get(1));
    }

    @Test
    public void countTestWithCondition() {
        List<Tuple> groupCount;
        storeAnswerDao(ID_ONE, POLL_NAME_A, ANSWER_LONG_10, true, ANSWER_KIWI);
        storeAnswerDao(ID_TWO, POLL_NAME_B, ANSWER_LONG_10, true, ANSWER_KIWI);
        storeAnswerDao(ID_THREE, POLL_NAME_B, ANSWER_LONG_10, false, ANSWER_APPLE);

        groupCount = pollAnswersDao.groupCount("pollName", where(isEqual("answerBoolean",true)));
        Assert.assertEquals(2,groupCount.size());
        Assert.assertEquals(1L,(long)groupCount.get(0).get(1));
        Assert.assertEquals(1L,(long)groupCount.get(1).get(1));

        groupCount = pollAnswersDao.groupCount("pollName", where(isEqual("answerBoolean",false)));
        Assert.assertEquals(1,groupCount.size());
        Assert.assertEquals(1L,(long)groupCount.get(0).get(1));
    }



}
