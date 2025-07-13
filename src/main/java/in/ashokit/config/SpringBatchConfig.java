package in.ashokit.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import in.ashokit.entity.Customer;
import in.ashokit.repo.CustomerRepository;
import lombok.AllArgsConstructor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

	private JobBuilderFactory jobBuilderFactory;
	private StepBuilderFactory stepBuilderFactory;
	private CustomerRepository customerRepository;

	// ##### Reader #####
	// create reader
	// it has to read the data and give the data as a customer object
	@Bean
	public FlatFileItemReader<Customer> customerReader() {
		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
		itemReader.setName("csv-reader");
		// header row will not be set in database it will be skip
		itemReader.setLinesToSkip(1);
		// you can efficiently convert lines of data into objects
		// converts a single line of input data into a domain object
		// get line data convert it into object
		// lineMapper - this function helps to each comma seperate value converted into java seperated value(customer object).
		// 1,John,Doe,john.doe@example.com,Male,1234567890,USA,1990-05-15

		// FlatFileItemReader reads a line from the CSV file.
		// Passes the line to lineMapper() for processing
		itemReader.setLineMapper(lineMapper());

		return itemReader;
	}

	private LineMapper<Customer> lineMapper() {

		//here I am creating the linemapper object
		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();


		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		// ["1", "John", "Doe", "john.doe@example.com", "Male", "1234567890", "USA", "1990-05-15"]
		// csv means comma seperated values
		// To parse each line of text into an array of values based on the specified delimiter.
		// what is mean by delimiter - to separate words, phrases, lines, and code snippets. The most common delimiters are the comma (,) and the full stop (.).
		// in csv file each line availabe in comma seperated
		lineTokenizer.setDelimiter(",");
		// one column value will not be there it will be consider like a null value.
		lineTokenizer.setStrict(false);
		// sequence of order of data columns.
		// These values are mapped to the specified column names
		// 		// ["1", "John", "Doe", "john.doe@example.com", "Male", "1234567890", "USA", "1990-05-15"]
		lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

		// it will take data from line mapper and to convert the data to (customer)bean object
		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		// once you read the data you need to store the data in one customer object.
		fieldSetMapper.setTargetType(Customer.class);

		// get data
		lineMapper.setLineTokenizer(lineTokenizer);
		// set data in customer object
		lineMapper.setFieldSetMapper(fieldSetMapper);

		return lineMapper;
	}

	// ##### processor #####
	// customer processor
	@Bean
	public CustomerProcessor customerProcessor() {
		return new CustomerProcessor();
	}

	// ##### Writer #####
	// item writer is used to save the csv file data to database
	@Bean
	public RepositoryItemWriter<Customer> customerWriter() {

		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
		writer.setRepository(customerRepository);
		// here we are set save method to save th data or insert the data
		writer.setMethodName("save");

		return writer;
	}
	
	// you can create multiple step
	@Bean
	public Step step() {
		// I want to process 10 records at a time like a chunk, and we need to pass reader,processor, writer
		return stepBuilderFactory.get("step-1").<Customer, Customer>chunk(10)
						  .reader(customerReader())
						  .processor(customerProcessor())
						  .writer(customerWriter())
						  .taskExecutor(taskExecutor())
						  .build();
	}

	// Invoke the step,
	// customers-import - job name
	// By default, Spring Batch automatically runs jobs when the application starts if a job exists in the Spring context.
	// Spring detects the batch job and executes it automatically.
	// that why after render it is job is calling and csv data is stored in database
	@Bean
	public Job job() {
		return jobBuilderFactory.get("customers-import")
								.flow(step())
								.end()
								.build();
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(10);
		return taskExecutor;
	}
	
	
}



// job launcher -> job -> step -> reader, processor, writer ->


// some user want to store data from csv to database, and some user get data from database and convert it into csv file.

// To "parse" means to analyze and convert data from one format into a more usable or structured format

// creation steps
// reader, processor, writer -> step -> job -> job launcher

