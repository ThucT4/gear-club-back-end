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

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.pw.model.Product;
import com.pw.repository.ProductRepository;

import com.pw.fetchdata;

@SpringBootApplication
public class GearClubApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(GearClubApplication.class);

	@Autowired
    private ProductRepository productRepository;

	public static void main(String[] args) throws IOException {
		// fetchdata.fetchData();
		// fixData();
		SpringApplication.run(GearClubApplication.class, args);
	}

	@Override
  	public void run(String... args) {
		log.info("StartApplication...");
		// System.out.println(Integer.parseInt(""));
		
		
		// Run the following 2 lines ONCE to initialize the table data
		// file2Db();
		// log.info("Successfully initialize table Products");
	}

	public static void fixData() {
		try {
			File inFile = new File("products.csv");
			FileReader input = new FileReader(inFile);
			CSVReader reader = new CSVReader(input);

			File outFile = new File("products2.csv");
			FileWriter output = new FileWriter(outFile, true);
			CSVWriter writer = new CSVWriter(output);

			String[] row;

			while ( (row = reader.readNext()) != null) {
				ArrayList<String> product = new ArrayList<String>();

				for (int i = 0; i < row.length; i++) {
					String cell = row[i];

					if (i == 1) {
						String[] redunt = {"nhà sản xuất:", "hãng sản xuất:", "thương hiệu:", "nhà sản xuất :", "·"};

						cell = cell.toLowerCase();

						for (String s : redunt) {
							cell = cell.replaceAll(s, "");
						}
					}

					else if (i == 2) {
						cell = cell.replaceAll(",", "");
						cell = cell.replaceAll("₫", "");
					}
					
					else if (i == 3) {
						String[] redunt = {"bảo hành", ":", " tháng", "·", "[^\\d]", " ́"};

						cell = cell.toLowerCase();

						for (String s : redunt) {
							cell = cell.replaceAll(s, "");
						}
						
						if (cell.contains(" tháng")) {
							System.out.println(cell);
						}
					}

					product.add(cell.trim());

            	}
				String[] data = product.toArray(new String[0]);

				writer.writeNext(data);
				
			}

			reader.close();
			writer.close();

		}
		catch (IOException event) {
			event.printStackTrace();
			log.info(event.toString());
		}
	}

	public void file2Db () {
		try {
			File file = new File("products2.csv");

			FileReader input = new FileReader(file);

			CSVReader reader = new CSVReader(input);

			String[] row;
			// String[] product = {name, vendorName, price, warranty, images.toString(), intro, features.toString()};

			String name = "", vendorName = "", price = "", warranty = "", intro = "";
			ArrayList<String> images = new ArrayList<>();
			HashMap<String, String> feature = new HashMap<>();

			
			while ( (row = reader.readNext()) != null) {
				for (int i = 0; i < row.length; i++) {
					String cell = row[i];

					if (i == 0) {
						name = cell;
					}

					else if (i == 1) {
						vendorName = cell;
					}

					else if (i == 2) {
						price = cell;
					}
					
					else if (i == 3) {
						warranty = cell;
					}

					else if (i == 4) {
						String listString = cell.substring(1, cell.length() - 1);

						images = new ArrayList<String>(Arrays.asList(listString.split(",")));
					}
					
					else if (i == 5) {
						intro = cell;
					}

					else if (i == 6) {
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
							}

							else {
								//add them to the hashmap and trim whitespaces
								feature.put(entry[0].trim(), entry[1].trim());  
							}        
						}
					}
            	}

				if (vendorName.length() > 50 || vendorName.length() < 2) {
					log.info(name);
				}

				Product product = new Product();

				product.setName(name);
				product.setImages(images);
				product.setVendorName(vendorName);
				product.setPrice(Long.parseLong(price));
				product.setFeatures(feature);
				product.setIntro(intro);

				product.setWarranty(warranty.isEmpty() ? 0 : Integer.parseInt(warranty));

				product.setCategory("mouse");

				try {
					productRepository.save(product);
				}
				catch (IllegalStateException e) {
					e.printStackTrace();
					log.info(e.toString());								
				}
			}
			

			reader.close();
		}
		catch (IOException event) {
			// event.printStackTrace();
			// log.info(event.toString());
		}
	}

}
