package embl.ebi.variation.examples.dynamicworkflow;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.job.DefaultJobParametersValidator;

public class SkippableStepsParametersValidator extends DefaultJobParametersValidator {
    
    public SkippableStepsParametersValidator() {
        super(new String[]{ "hello" }, // mandatory parameters
              new String[]{ }); // optional parameters
    }

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        super.validate(parameters);
        
        if (parameters.getString("doStep2") != null) {
            try {
                Boolean.parseBoolean(parameters.getString("doStep2", "true"));
            } catch (Exception e) {
                throw new JobParametersInvalidException("Step 2 option incorrect");
            }
        }
        
        if (parameters.getString("doStep3") != null) {
            try {
                Boolean.parseBoolean(parameters.getString("doStep3", "true"));
            } catch (Exception e) {
                throw new JobParametersInvalidException("Step 3 option incorrect");
            }
        }
        
        if (parameters.getString("doStep4") != null) {
            try {
                Boolean.parseBoolean(parameters.getString("doStep4", "true"));
            } catch (Exception e) {
                throw new JobParametersInvalidException("Step 4 option incorrect");
            }
        }
    }

}
