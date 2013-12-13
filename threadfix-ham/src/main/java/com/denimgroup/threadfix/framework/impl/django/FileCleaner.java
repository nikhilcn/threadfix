package com.denimgroup.threadfix.framework.impl.django;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileCleaner {

	public File fileCleaner(File file) {

		String filePath = file.toString();
		try {

			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			filePath = file.toString();
			int trimmer = filePath.lastIndexOf("\\");
			filePath = filePath.substring(0, trimmer);
			filePath = filePath + "\\urls.txt";

			FileWriter fw = new FileWriter(filePath);
			String line;
			String cline = "";
			boolean flag = false;
			boolean writer = true;

			while ((line = br.readLine()) != null) {
				if (!line.contains("#")) {
					if (flag == true) {

						line = line.trim(); // remove leading and trailing//
											// whitespace
						if (!line.equals("")) // don't write out blank lines
						{
							cline = cline + line;

						}
						if ((line.contains("'),")) | (line.contains("\"),"))
								| (line.contains("\")),"))
								| (line.contains("')),")) | (line.isEmpty())) {
							flag = false;
							writer = true;
							fw.append(cline);

							cline = "";
							//
							fw.write("\r\n");
						}
					} else {
						line = line.trim();

						if (line.contains("url(")) {
							br.mark(1000);
							if (!br.readLine().contains("url(")) {
								flag = true;

								writer = false;
								cline = cline + "" + line;

							}
							br.reset();
						}

						if (writer == true) {
							line = line + "\r\n";

							fw.write(line, 0, line.length());

						}
					}
				}
			}
			fr.close();
			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		File rfile = new File(filePath);
		return rfile;
	}
}
