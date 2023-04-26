package com.pw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collector;

import javax.validation.constraints.Null;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootPostgresqlJpaCrudRestApplication {

	public static void main(String[] args) throws IOException {
		fetchData();
		// SpringApplication.run(SpringBootPostgresqlJpaCrudRestApplication.class, args);
	}

	public static void fetchData() throws IOException {
		System.out.println("Entered");

		Document doc = Jsoup.connect("https://gearvn.com/pages/chuot-may-tinh").get();

		Elements links = doc.select("a[href~=https://gearvn.com/products]");

		int count = 0;

		for (Element link : links) {
			try {
				String name = "", vendorName = "", warranty = "",
						price = "", intro = "";

				ArrayList<String> images = new ArrayList<String>();

				HashMap<String, String> features = new HashMap<String, String>();

				Document curDoc = Jsoup.connect(link.attr("href")).get();

				name  = curDoc.selectFirst("h1.product_name").text();

				Elements spans = curDoc.select("span");

				for (Element span : spans) {
					// Get vendor name
					if (span.text().toLowerCase().contains("nhà sản xuất") || span.text().toLowerCase().contains("hãng sản xuất")
						|| span.text().toLowerCase().contains("thương hiệu")) {
						vendorName = span.text();
					}

					// Get warranty
					if (span.text().toLowerCase().contains("bảo hành")) {
						warranty = span.text();
					}
				}

				// Get price
				price = curDoc.selectFirst("span.product_sale_price").text();

				// Get images
				Elements imgs = curDoc.selectFirst("div.fotorama").children();

				for (Element img : imgs) {
					images.add(img.attr("src"));
				}

				// Get features
				Elements trs = curDoc.select("tr.row-info");

				for (Element tr : trs) {
					Elements tds = tr.select("td");

					String featureName = "";

					for (int i = 0; i < tds.size(); i++ ) {
						Element td = tds.get(i);

						if (i % 2 == 0) {
							featureName = td.text();
						}
						else {
							features.put(featureName, td.text());
						}
					}
				}

				// Get introduction
				Element e = curDoc.selectFirst("h1:contains(Đánh giá)");

				if (e == null) {
					e = curDoc.selectFirst("h2:contains(Đánh giá)");
				} 

				if (e.nextElementSibling() != null) {
					intro = e.nextElementSibling().text();
				} 
				else {
					System.out.println(e.nextElementSibling());
					System.out.println(link.attr("href"));
				}

				System.out.println(intro);


				



				// System.out.println(vendorE.size());
				count++;
			} 
			catch (HttpStatusException e) {
				// System.out.println(link.attr("href"));
			}
		}

		System.out.println(count);
	}

}
