package in.ashokit.rest;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {

	// job launcher is useful for launch the job
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job job;
	
	@GetMapping("/import")
	public void loadDataToDB() throws Exception{

		// when job this job will be start / i have ste the current time
		JobParameters jobParams = new JobParametersBuilder()
											.addLong("startAt", System.currentTimeMillis()).toJobParameters();
		// from here I am launching the job and I have set jobParam as well
		jobLauncher.run(job, jobParams);
	}
	
	
}

//important notes -

// why we use batch processing it take only 2 to 3 sec to store thousand of data to database from csv file to database
// if you want to process bulk of data spring batch is the best way
// in console you check 1000 queries will be execuated
// at a time only 10 records will be execuated - I have set 10 as a chunk
// at a time 10 queries will be execuated
// first select query will be calledto check in csv  records are present or not then it will be inserted



//How Can I Call the Job Explicitly Without @Autowired?
//If you don't want to use @Autowired, you can manually fetch the job from the Spring context like this:
//
//java
//		Copy
//Edit
//@Autowired
//private ApplicationContext applicationContext;
//
//@GetMapping("/import")
//public void loadDataToDB() throws Exception {
//	Job job = (Job) applicationContext.getBean("customers-import");  // Fetching job by name
//
//	JobParameters jobParams = new JobParametersBuilder()
//			.addLong("startAt", System.currentTimeMillis())
//			.toJobParameters();
//
//	jobLauncher.run(job, jobParams);
//}
