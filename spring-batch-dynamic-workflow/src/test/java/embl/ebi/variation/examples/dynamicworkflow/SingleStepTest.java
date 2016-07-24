package embl.ebi.variation.examples.dynamicworkflow;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created on 2016-07-24.
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SimpleDeciderConfiguration.class, JobLauncherTestUtils.class})
public class SingleStepTest {
	
	@Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
	
	@Test
	public void testWholeJob() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
	}

	@Test
	public void testStep1() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("step1");
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());		
	}

	@Test
	public void testStep2() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("step2");
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());		
	}

	@Test
	public void testStep3() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("step3");
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());		
	}

	@Test
	public void testStep4() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("step4");
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());		
	}
}
