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

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.builder.TaskletStepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@EnableBatchProcessing
public class SimpleDeciderConfiguration {

    public static final String jobName = "simpleDeciderJob";
    private static final int duration = 1000;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    
    @Bean
    public Job simpleDeciderJob() {
        JobBuilder jobBuilder = jobBuilderFactory
                .get(jobName)
                .incrementer(new RunIdIncrementer());

        StepTwoDecider s2d = new StepTwoDecider();
        StepThreeDecider s3d = new StepThreeDecider();
        StepFourDecider s4d = new StepFourDecider();
        
        // Step 2 is optional
        Flow flowOptional = new FlowBuilder<Flow>("optionalflow")
                .start(s2d).on("DO_STEP_2").to(step2())
                .from(s2d).on("SKIP_STEP_2").end("COMPLETED")
                .build();
                
        // Steps 3 and 4 are optional and parallel
        Flow flowSplit = new FlowBuilder<Flow>("splitflow")
                .split(new SimpleAsyncTaskExecutor())
                .add(
                        new FlowBuilder<Flow>("subflow_step_3")
                                .from(s3d).on("DO_STEP_3").to(step3())
                                .from(s3d).on("SKIP_STEP_3").end("COMPLETED").build(),
                        new FlowBuilder<Flow>("subflow_step_4")
                                .from(s4d).on("DO_STEP_4").to(step4())
                                .from(s4d).on("SKIP_STEP_4").end("COMPLETED").build())
                .build();

        /*
         * Complete workflow:
         * 
         *                |--> (S3)
         *  S1 ---> (S2) -+
         *                |--> (S4)
         */
        FlowJobBuilder builder = jobBuilder
                .flow(step1())
                .next(flowOptional)
                .next(flowSplit)
                .end();
        
        return builder.build();
    }

    public Step step1() {
        StepBuilder step1 = stepBuilderFactory.get("step1");
        TaskletStepBuilder tasklet = step1.tasklet(new Tasklet() {
            
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("*** Running step 1");
                Thread.sleep(duration);
                return RepeatStatus.FINISHED;
            }
        });

        // true: every job execution will do this step, even if this step is already COMPLETED
        // false: if the job was aborted and is relaunched, this step will NOT be done again
        tasklet.allowStartIfComplete(false);

        return tasklet.build();
    }
    
    public Step step2() {
        StepBuilder step1 = stepBuilderFactory.get("step2");
        TaskletStepBuilder tasklet = step1.tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("*** Running step 2");
                Thread.sleep(duration);
                return RepeatStatus.FINISHED;
            }
        });

        // true: every job execution will do this step, even if this step is already COMPLETED
        // false: if the job was aborted and is relaunched, this step will NOT be done again
        tasklet.allowStartIfComplete(false);

        return tasklet.build();
    }
    
    public Step step3() {
        StepBuilder step1 = stepBuilderFactory.get("step3");
        TaskletStepBuilder tasklet = step1.tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("*** Running step 3");
                Thread.sleep(duration);
                return RepeatStatus.FINISHED;
            }
        });

        // true: every job execution will do this step, even if this step is already COMPLETED
        // false: if the job was aborted and is relaunched, this step will NOT be done again
        tasklet.allowStartIfComplete(false);

        return tasklet.build();
    }
    
    public Step step4() {
        StepBuilder step1 = stepBuilderFactory.get("step4");
        TaskletStepBuilder tasklet = step1.tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("*** Running step 4");
                Thread.sleep(duration);
                return RepeatStatus.FINISHED;
            }
        });

        // true: every job execution will do this step, even if this step is already COMPLETED
        // false: if the job was aborted and is relaunched, this step will NOT be done again
        tasklet.allowStartIfComplete(false);

        return tasklet.build();
    }
    
}