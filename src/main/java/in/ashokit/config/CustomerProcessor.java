package in.ashokit.config;

import org.springframework.batch.item.ItemProcessor;

import in.ashokit.entity.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer item) throws Exception {

		// logic

		// whatever procession logic you can do it here
		// if country is Indai only then data will be return
     //   if(item.getCountry().equals("India")){
		// return item;
		// }
//		return null;

		// I don't want to process any data so i have just return the item
		return item;
	}

}



// we can process like this also

//@Bean
//public ItemProcessor<Customer, Customer> customerProcessor() {
//	return customer -> {
//		// Example: Convert email to lowercase before saving
//		customer.setEmail(customer.getEmail().toLowerCase());
//		return customer;
//	};
//}
