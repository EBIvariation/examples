package uk.ac.ebi.eva;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.eva.persistence.dao.UserDao;
import uk.ac.ebi.eva.persistence.entities.User;
import uk.ac.ebi.eva.persistence.repository.CountGroupByRepositoryImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = UserDao.class)
@EnableJpaRepositories(basePackages = {"uk.ac.ebi.eva.persistence.dao","uk.ac.ebi.eva.persistence.repository"}, repositoryBaseClass = CountGroupByRepositoryImpl.class)
public class BasicTest {

    private static final String TEST_MAIL = "testMail";
    private static final String TEST_MAIL2 = "testMail2";
    private static final String TEST_PASSWORD = "testPassword";
    private static final String TEST_UNIQUE = "testUnique";

    @Autowired
    private UserDao userDao;

    private void testCreateUser(String email, String password, String uniqueField) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setUniqueField(uniqueField);

        long count = userDao.count();
        userDao.save(user);
        Assert.assertEquals(count + 1, userDao.count());
    }

    @Test
    public void testCreateUser() {
        testCreateUser(TEST_MAIL,TEST_PASSWORD,TEST_UNIQUE);
    }

    @Test
    public void testGetByMail(){
        testCreateUser();
        User user = userDao.findByEmail(TEST_MAIL);
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getPassword(),TEST_PASSWORD);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicateMail() {
        testCreateUser(TEST_MAIL,TEST_PASSWORD,TEST_UNIQUE);
        testCreateUser(TEST_MAIL2,TEST_PASSWORD,TEST_UNIQUE);
    }

}