package embl.ebi.variation.examples.dynamicworkflow;

import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import embl.ebi.variation.examples.parameters.StepOneData;

public class StepOne implements Tasklet {

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<String, Object> parameters = chunkContext.getStepContext().getJobParameters();
		StepOneData stepOneData = new StepOneData(String.valueOf(parameters.get("name")));
		
		System.out.println("*** Running step 1 with name " + stepOneData.getName());
        Thread.sleep(SimpleDeciderConfiguration.DURATION);
        return RepeatStatus.FINISHED;
	}

}
