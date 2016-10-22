package embl.ebi.variation.examples.dynamicworkflow;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SimpleDeciderConfiguration.class, JobLauncherTestUtils.class })
public class StepTest {

    @Autowired
    private JobLauncherTestUtils jobLauncher;

    @Test
    public void testStep1() {
        JobParameters parameters = new JobParametersBuilder()
                .addString("name", "MyName")
//                .addString("doStep2", "true")
                .addString("doStep3", "true")
                .toJobParameters();

        JobExecution execution = jobLauncher.launchStep("step1", parameters);

        assertEquals("COMPLETED", execution.getExitStatus().getExitCode());
        assertEquals(1, execution.getStepExecutions().size());
    }

//    @Test
//    public void testStep2() {
//        fail("Not implemented yet");
//    }
//
//    @Test
//    public void testStep3() {
//        fail("Not implemented yet");
//    }
//
//    @Test
//    public void testStep4() {
//        fail("Not implemented yet");
//    }
}
