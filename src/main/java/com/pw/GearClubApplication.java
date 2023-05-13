package com.pw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.pw.model.Customer;
import com.pw.model.Role;
import com.pw.repository.CustomerRepository;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.pw.model.Collection;
import com.pw.model.Product;
import com.pw.repository.CollectionRepository;
import com.pw.repository.ProductRepository;

import com.pw.fetchdata;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class GearClubApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(GearClubApplication.class);

	@Autowired
    private ProductRepository productRepository;

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(GearClubApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("StartApplication...");

		// Run the following 2 lines ONCE to initialize the table data
//		file2Db("products.csv");
//		initCollection();
//		initCustomer();
//		log.info("Successfully initialize data");
	}

	public void file2Db (String fileName) {
		try {
			File file = new File(fileName);

			FileReader input = new FileReader(file);

			CSVReader reader = new CSVReader(input);

			String[] row;
			//

			String name = "", vendorName = "", price = "", warranty = "", intro = "", designLoc = "", titleDescrip = "", description = "", cate = "";
			Integer quantity = 0;
			ArrayList<String> images = new ArrayList<>();
			HashMap<String, String> feature = new HashMap<>();
			HashMap<String, String> highlights = new HashMap<>();


			while ( (row = reader.readNext()) != null) {
				for (int i = 0; i < row.length; i++) {
					String cell = row[i];

					if (i == 0) {
						name = cell;
					} else if (i == 1) {
						vendorName = cell;
					} else if (i == 2) {
						price = cell;
					} else if (i == 3) {
						designLoc = cell;
					} else if (i == 4) {
						warranty = cell;

						if (warranty.isEmpty()) {
							warranty = String.valueOf(ThreadLocalRandom.current().nextInt(1, 24));
						}
					} else if (i == 5) {
						String listString = cell.substring(1, cell.length() - 1);

						images = new ArrayList<String>(Arrays.asList(listString.split(",")));
					} else if (i == 6) {
						intro = cell;
					} else if (i == 7) {
						titleDescrip = cell;
					} else if (i == 8) {
						description = cell;
					} else if (i == 9) {
						//remove curly brackets
						String value = cell.substring(1, cell.length()-1);

						//split the string to creat key-value pairs
						String[] keyValuePairs = value.split(",");


						feature = new HashMap<String,String> ();

						//iterate over the pairs
						for(String pair : keyValuePairs) {
							//split the pairs to get key and value
							String[] entry = pair.split("=");

							if (entry.length < 2) {
								System.out.println(pair);
							} else {
								//add them to the hashmap and trim whitespaces
								feature.put(entry[0].trim(), entry[1].trim());
							}
						}
					} else if (i == 10) {
						//remove curly brackets
						String value = cell.substring(1, cell.length()-1);

						//split the string to creat key-value pairs
						String[] keyValuePairs = value.split(",");


						highlights = new HashMap<String,String> ();

						//iterate over the pairs
						for(String pair : keyValuePairs) {
							//split the pairs to get key and value
							String[] entry = pair.split("=");

							if (entry.length < 2) {
								System.out.println(pair);
							} else {
								//add them to the hashmap and trim whitespaces
								highlights.put(entry[0].trim(), entry[1].trim());
							}
						}
					} else if (i == 11) {
						cate = cell;
					} else if (i == 12) {
						quantity = Integer.parseInt(cell);
					}
				}

				Product product = new Product();

				product.setName(name);
				product.setImages(images);
				product.setVendorName(vendorName);
				product.setWarranty(Integer.parseInt(warranty));
				product.setPrice(Long.parseLong(price));
				product.setFeatures(feature);
				product.setIntro(intro);
				product.setTitle(titleDescrip);
				product.setDescription(description);
				product.setDesignLocation(designLoc);

				product.setWarranty(warranty.isEmpty() ? 0 : Integer.parseInt(warranty));

				product.setCategory(cate);

				product.setQuantity(quantity);
				product.setHighlights(highlights);

				try {
					productRepository.save(product);
				} catch (IllegalStateException e) {
					e.printStackTrace();
					log.info(e.toString());
				}
			}
			reader.close();
		} catch (IOException event) {
			// event.printStackTrace();
			// log.info(event.toString());
		}
	}

	public void initCollection () {
		try {
			File file = new File("collections.csv");

			FileReader input = new FileReader(file);

			CSVReader reader = new CSVReader(input);

			String[] row;

			String name = "";
			HashMap<Integer, String> productList = new HashMap<>();

			while ( (row = reader.readNext()) != null) {
				for (int i = 0; i < row.length; i++) {
					String cell = row[i];

					if (i == 0) {
						name = cell;
					} else {
						//remove curly brackets
						String value = cell.substring(1, cell.length()-1);

						//split the string to creat key-value pairs
						String[] keyValuePairs = value.split(",");


						productList = new HashMap<Integer,String> ();

						//iterate over the pairs
						for(String pair : keyValuePairs) {
							//split the pairs to get key and value
							String[] entry = pair.split("=");

							if (entry.length < 2) {
								System.out.println(pair);
							} else {
								//add them to the hashmap and trim whitespaces
								productList.put(Integer.parseInt(entry[0].trim()), entry[1].trim());
							}
						}
					}
				}

				Collection collection = new Collection();

				collection.setName(name);
				collection.setProductList(productList);

				collectionRepository.save(collection);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void initCustomer() {
		// Create regular user
		ArrayList<HashMap<Integer, Integer>> listOfShoppingCart = new ArrayList<>();
		HashMap<Integer, Integer> firstShoppingCart = new HashMap<>();
		firstShoppingCart.put(-1, 0); // Index of first cart in cart list
		firstShoppingCart.put(-2, 1); // Status of first cart
		listOfShoppingCart.add(firstShoppingCart);

		Customer regularUser = Customer.builder()
				.email("regular_customer@gearclub.com")
				.firstName("Thinh")
				.lastName("Ngo")
				.password(passwordEncoder.encode("Regularcustomer123@"))
				.phone("0382210397")
				.role(Role.USER)
				.shippingAddress("1116 Huynh Tan Phat, District 7, Ho Chi Minh city")
				.shoppingCart(listOfShoppingCart)
				.build();

		customerRepository.save(regularUser);



		// Create admin user
		ArrayList<HashMap<Integer, Integer>> listOfShoppingCart2 = new ArrayList<>();
		HashMap<Integer, Integer> firstShoppingCart2 = new HashMap<>();
		firstShoppingCart2.put(-1, 0); // Index of first cart in cart list
		firstShoppingCart2.put(-2, 1); // Status of first cart
		listOfShoppingCart2.add(firstShoppingCart2);

		Customer adminUser = Customer.builder()
				.email("admin@gearclub.com")
				.firstName("Admin")
				.lastName("Admin")
				.password(passwordEncoder.encode("Admin123@"))
				.phone("")
				.role(Role.ADMIN)
				.shippingAddress("")
				.shoppingCart(listOfShoppingCart2)
				.build();

		customerRepository.save(adminUser);
	}
}
