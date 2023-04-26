public class fetchdata {
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
					if (span.text().toLowerCase().contains("nhà sản xuất:") || span.text().toLowerCase().contains("hãng sản xuất:")
						|| span.text().toLowerCase().contains("thương hiệu:") 
						|| span.text().toLowerCase().contains("nhà sản xuất :")) {
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
							System.out.println(link.attr("href"));
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
