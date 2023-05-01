package com.pw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import com.opencsv.CSVWriter;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

public class fetchdata {
	public static void fetchData2() throws IOException {
		System.out.println("Entered");

		Document doc = Jsoup.connect("https://www.phongcachxanh.vn/collections/lot-chuot").get();

		Elements linkEs = doc.select("a[href~=/products/lot-chuot]");
		List<String> links = linkEs.stream().map(e -> e.attr("href")).collect(Collectors.toList());

		System.out.println(links.size());
		List<String> distincLinks = new ArrayList<>(new HashSet<>(links));
		System.out.println(distincLinks.size());

		for (String link : distincLinks) {
			String name = "", vendorName = "", warranty = "",
						price = "", intro = "", designLoc = "", description = "", titleDescrip = "";

			ArrayList<String> images = new ArrayList<String>();

			HashMap<String, String> features = new HashMap<String, String>();
			HashMap<String, String> highlights = new HashMap<String, String>();


			Document curDoc = Jsoup.connect("https://www.phongcachxanh.vn/" + link).get();

			name = curDoc.selectFirst("h1[class~=product-info__title]").text();

			vendorName = curDoc.selectFirst("div.product-info__vendor").text();

			price = curDoc.selectFirst("sale-price").text().replaceAll("[^\\d]", "");;

			Elements thumbnail = curDoc.select("page-dots.product-gallery__thumbnail-list").select("button.product-gallery__thumbnail");

			for (Element thumb : thumbnail) {
				String imgLink = thumb.selectFirst("img").attr("src");

				images.add("https:" + imgLink);
			}

			if (images.size() < 1 ) {
				images.add("https://cdn.shopify.com/s/files/1/0636/9044/0949/products/lot-chu-t-glorious-elements-air-mouse-pad-xl-37010435834101.png?v=1677169275&width=700");
			}

			
			String[] locs = {"USA", "korea", "denmark"};

			designLoc = locs[ThreadLocalRandom.current().nextInt(0, 3)];

			intro = "Đế poron vừa chống trượt hoàn hảo, vừa tạo độ mềm vừa phải giúp tăng stopping power (khả năng dừng chuột tức thời) sau mỗi cú flick chuột.";

			if (!curDoc.select("strong:contains(Bảo hành:)").isEmpty()) {
				warranty = curDoc.selectFirst("strong:contains(Bảo hành:)").text().replaceAll("[^\\d]", "");
				
			}


			if (curDoc.select("div.section-stack__intro").isEmpty()) {
				titleDescrip = "Bề mặt vải Polyester cao cấp";
				description = "Được thiết kế tập trung vào độ control, dừng chuột và tracking. Mặt vải mềm trung tính phù hợp và dễ làm quen trong nhiều nhu cầu sử dụng.";
				// titleDescrip = "Đối xứng - PAW3395";
				// description = name + " là mẫu chuột đối xứng giúp bạn chơi game và giành lấy những vị trí cao thật cao trên bảng xếp hạng. Trọng lượng khoảng dưới 60 gram, thiết kế không lỗ cùng thời lượng pin lên đến 70 giờ giúp bạn sử dụng chuột cả ngày dài mà không lo mệt mỏi như những mẫu chuột không dây nặng nề truyền thống.";
				
				// features.put("Kết nối không dây", "LIGHTSPEED");
				// features.put("Độ chính xác tiên tiến", "CẢM BIẾN HERO 25K");
				// features.put("Tương thích sạc không dây", "POWERPLAY");

				// highlights.put("LIGHTSPEED", "Kết nối không dây");
				// highlights.put("CẢM BIẾN HERO 25K", "Độ chính xác tiên tiến");
				// highlights.put("POWERPLAY", "Tương thích sạc không dây");

				features.put("Loại bề mặt", "Control");
				features.put("Chất liệu đế", "Poron");
				features.put("Tương thích sạc không dây", "Poron");

				highlights.put("Control", "Loại bề mặt");
				highlights.put("CẢM BIẾN HERO 25K", "Độ chính xác tiên tiến");
				highlights.put("Poron", "Chất liệu đế");
			}

			else {

				if (curDoc.selectFirst("div.section-stack__intro").select("h2").isEmpty()) {
					titleDescrip = "Bề mặt vải Polyester cao cấp";
				} 
				else {
					titleDescrip = curDoc.selectFirst("div.section-stack__intro").selectFirst("h2").text();
				}

				description = curDoc.selectFirst("div.section-stack__intro").selectFirst("div.metafield-rich_text_field").text();
				
				Elements rows = curDoc.selectFirst("div[class~=feature-chart__table]").select("div.feature-chart__table-row");

				for (Element row : rows) {
					String fName = row.selectFirst("div[class~=feature-chart__heading]").text();
					String feature = row.selectFirst("div[class~=feature-chart__value]").text().replaceAll(",", " ");

					features.put(fName, feature);
				}

				Elements bigs = curDoc.select("impact-text");
				Elements smalls = curDoc.select("div.impact-text__content");

				for (int i = 0; i < bigs.size(); i++) {
					highlights.put(bigs.get(i).text(), smalls.get(i).text());
				}
			}

			String[] pad = {name, vendorName, price, designLoc, warranty, images.toString(), intro, titleDescrip, description, features.toString(), highlights.toString()};

			try {
				File file = new File("pads.csv");
	
				FileWriter output = new FileWriter(file, true);
	
				CSVWriter writer = new CSVWriter(output);
	
				writer.writeNext(pad);
	
				writer.close();
			}
			catch (IOException event) {
				System.out.println(pad);
				event.printStackTrace();
			}
		}
	}

	public static void initializeCollection() throws IOException{
		File file = new File("collections.csv");
	
		FileWriter output = new FileWriter(file, true);

		CSVWriter writer = new CSVWriter(output);

		HashMap<Integer, String> productList = new HashMap<>( );

		// productList.put(62, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/chuot-khong-day-sieu-nhe-finalmouse-starlight-pro-tenz_4.jpg?v=1673945400&width=500");
		// productList.put(7, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/banner-Lamzu-Atlantis.jpg?v=1676368457&width=1000");
		// productList.put(41, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/banner-Pulsar-x2.jpg?v=1676368662&width=1000");
		// productList.put(69, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/banner_Series_One_Pro.jpg?v=1676368587&width=1000");
		// productList.put(4, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/banner-grip_corepad.jpg?v=1676368784&width=1000");
		// productList.put(148, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/banner-feet-corepad.jpg?v=1676368797&width=1000");
		// productList.put(111, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/Pulsar_x_Bruce_Lee_ES1_gaming_mouse_pad_XL_01_98c2cabe-0c7b-4672-b47a-be5cf9c20bb6.jpg?v=1679554051&width=800");

		// productList.put(43, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/pad-mem-1500x1500.jpg?v=1676445325&width=1000");
		// productList.put(9, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/pad-cung-1500x1500.jpg?v=1676445339&width=1000");

		productList.put(163, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/V30Filco-convertible-3-1500x1500.jpg?v=1676456283&width=1000");
		productList.put(148, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/Minila-r-1500x1500.jpg?v=1676445447&width=1000");
		productList.put(137, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/V2-GMMK-pro-1500x1000.jpg?v=1676445447&width=1000");
		productList.put(133, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/GMMK2-1500x1000.jpg?v=1676445448&width=1000");
		productList.put(48, "https://cdn.shopify.com/s/files/1/0636/9044/0949/files/keycap-1500x1000.jpg?v=1676448363&width=1400");
		
		

		String[] collection = {"Trend3", productList.toString()};

		writer.writeNext(collection);

		writer.close();
	}

    public static void fetchData() throws IOException {
		System.out.println("Entered");

		Document doc = Jsoup.connect("https://gearvn.com/pages/chuot-may-tinh").get();

		Elements linkEs = doc.select("a[href~=https://gearvn.com/products]");

		List<String> links = linkEs.stream().map(e -> e.attr("href")).collect(Collectors.toList());

		List<String> distincLinks = links.stream().distinct().collect(Collectors.toList());

		int count = 0;

		for (String link : distincLinks) {
			try {
				String name = "", vendorName = "", warranty = "",
						price = "", intro = "";

				ArrayList<String> images = new ArrayList<String>();

				HashMap<String, String> features = new HashMap<String, String>();

				Document curDoc = Jsoup.connect(link).get();

				name  = curDoc.selectFirst("h1.product_name").text();

				Elements spans = curDoc.select("span");

				for (Element span : spans) {
					// Get vendor name
					if (span.text().toLowerCase().contains("nhà sản xuất:") || span.text().toLowerCase().contains("hãng sản xuất:")
						|| span.text().toLowerCase().contains("thương hiệu:") 
						|| span.text().toLowerCase().contains("nhà sản xuất :")) {
						
						if (vendorName.isEmpty()) {
							vendorName = span.text();
						}	
					}

					// Get warranty
					if (span.text().toLowerCase().contains("bảo hành:") || span.text().toLowerCase().contains("bảo hành :")) {
						if (warranty.isEmpty()) {
							warranty = span.text();
						} 
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
							features.put(featureName, td.text().replaceAll(",", "."));
						}
					}
				}

				// Get introduction
				try {
					Element e = curDoc.selectFirst("h1:contains(chi tiết)");

					if (e == null) {
						e = curDoc.selectFirst("h2:contains(chi tiết)");
					} 

					if (e == null) {
						e = curDoc.selectFirst("h3:contains(chi tiết)");
					}

					if (e == null) {
						e = curDoc.selectFirst("strong:contains(Tính năng)");
					}

					Element introE;

					if (e == null) {
						introE = curDoc.selectFirst("p.MsoNormal");
					}
					else {
						introE = e.nextElementSibling();

						while (!introE.tagName().equals("p") || introE.text().isEmpty()) {
							introE = introE.nextElementSibling();
						}

						if (!introE.tagName().equals("p") || introE.text().isEmpty()) {
							System.out.println(link);
						}
					}

					if (!(introE == null)) {
						System.out.println("Writing to file");
						intro = introE.text();

						String[] product = {name, vendorName, price, warranty, images.toString(), intro, features.toString()};

						System.out.println(product);
						write2File(product);

						count++;
					}
					else {
						// System.out.println(link.attr("href"));
					}

				} 
				catch (NullPointerException e) {
					// System.out.println(link.attr("href"));
				}
				
			} 
			catch (HttpStatusException e) {
				// System.out.println(link.attr("href"));
			}
		}

		System.out.println(count);
	}

	public static void write2File(String[] data) {
		try {
			File file = new File("products.csv");

			FileWriter output = new FileWriter(file, true);

			CSVWriter writer = new CSVWriter(output);

			// String[] header = {"Name", "Vendor Name", "Price", "Warranty", "Image", "Intro", "Feature"};

			// writer.writeNext(header);

			writer.writeNext(data);

			writer.close();
		}
		catch (IOException event) {
			System.out.println(data);
			event.printStackTrace();
		}
	}
}
