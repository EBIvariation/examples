/*
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package embl.ebi.variation.examples.dynamicworkflow;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by jmmut on 2015-10-14.
 *
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SimpleDeciderConfiguration.class})
public class SimpleDeciderConfigurationTest {

    @Autowired
    private Job job;
    @Autowired
    private JobLauncher jobLauncher;

    @Test
    public void testAllStepsDone() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addString("doStep2", "true")
                .addString("doStep3", "true")
                .toJobParameters();

        JobExecution execution = jobLauncher.run(job, parameters);

        assertEquals("COMPLETED", execution.getExitStatus().getExitCode());
        assertEquals(4, execution.getStepExecutions().size());
        
        List<StepExecution> steps = new ArrayList<>(execution.getStepExecutions());
        StepExecution step1 = steps.get(0);
        StepExecution step2 = steps.get(1);
        StepExecution parallelStep1 = steps.get(2);
        StepExecution parallelStep2 = steps.get(3);
        
        assertEquals("step1", step1.getStepName());
        assertEquals("step2", step2.getStepName());
        assertTrue(("step3".equals(parallelStep1.getStepName()) && "step4".equals(parallelStep2.getStepName())) 
                || ("step4".equals(parallelStep1.getStepName()) && "step3".equals(parallelStep2.getStepName())));
        assertTrue(step1.getEndTime().before(step2.getStartTime()));
        assertTrue(step2.getEndTime().before(parallelStep1.getStartTime()));
        assertTrue(step2.getEndTime().before(parallelStep2.getStartTime()));
    }

    @Test
    public void testOnlyFirstAndLastDone() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addString("doStep2", "false")
                .addString("doStep3", "false")
                .toJobParameters();

        JobExecution execution = jobLauncher.run(job, parameters);

        assertEquals("COMPLETED", execution.getExitStatus().getExitCode());
        assertEquals(2, execution.getStepExecutions().size());
        
        List<StepExecution> steps = new ArrayList<>(execution.getStepExecutions());
        StepExecution step1 = steps.get(0);
        StepExecution step4 = steps.get(1);
        
        assertEquals("step1", step1.getStepName());
        assertEquals("step4", step4.getStepName());
        assertTrue(step1.getEndTime().before(step4.getStartTime()));
    }

    @Test
    public void testSkipStep2() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addString("doStep2", "false")
                .toJobParameters();

        JobExecution execution = jobLauncher.run(job, parameters);
        assertEquals(3, execution.getStepExecutions().size());

        assertEquals("COMPLETED", execution.getExitStatus().getExitCode());
        
        List<StepExecution> steps = new ArrayList<>(execution.getStepExecutions());
        StepExecution step1 = steps.get(0);
        StepExecution parallelStep1 = steps.get(1);
        StepExecution parallelStep2 = steps.get(2);
        
        assertEquals("step1", step1.getStepName());
        assertTrue(("step3".equals(parallelStep1.getStepName()) && "step4".equals(parallelStep2.getStepName())) 
                || ("step4".equals(parallelStep1.getStepName()) && "step3".equals(parallelStep2.getStepName())));
        assertTrue(step1.getEndTime().before(parallelStep1.getStartTime()));
        assertTrue(step1.getEndTime().before(parallelStep2.getStartTime()));
    }

    @Test
    public void testSkipStep3() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addString("doStep3", "false")
                .toJobParameters();

        JobExecution execution = jobLauncher.run(job, parameters);

        assertEquals("COMPLETED", execution.getExitStatus().getExitCode());
        assertEquals(3, execution.getStepExecutions().size());
        
        List<StepExecution> steps = new ArrayList<>(execution.getStepExecutions());
        StepExecution step1 = steps.get(0);
        StepExecution step2 = steps.get(1);
        StepExecution step4 = steps.get(2);
        
        assertEquals("step1", step1.getStepName());
        assertEquals("step2", step2.getStepName());
        assertEquals("step4", step4.getStepName());
        assertTrue(step1.getEndTime().before(step2.getStartTime()));
        assertTrue(step2.getEndTime().before(step4.getStartTime()));
    }

    @Test
    public void testSkipStep4() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addString("doStep4", "false")
                .toJobParameters();

        JobExecution execution = jobLauncher.run(job, parameters);

        assertEquals("COMPLETED", execution.getExitStatus().getExitCode());
        assertEquals(3, execution.getStepExecutions().size());
        
        List<StepExecution> steps = new ArrayList<>(execution.getStepExecutions());
        StepExecution step1 = steps.get(0);
        StepExecution step2 = steps.get(1);
        StepExecution step3 = steps.get(2);
        
        assertEquals("step1", step1.getStepName());
        assertEquals("step2", step2.getStepName());
        assertEquals("step3", step3.getStepName());
        assertTrue(step1.getEndTime().before(step2.getStartTime()));
        assertTrue(step2.getEndTime().before(step3.getStartTime()));
    }

}
