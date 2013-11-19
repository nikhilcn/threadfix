package com.denimgroup.threadfix.framework.impl.django;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.LinkedHashMultimap;

public class Constructendpoints {

	public LinkedHashMultimap<Object, Object> constructEndPoints(
			ArrayList<String> baseEndPointsList, String rootPath) {

		LinkedHashMultimap<Object, Object> pointsMap = LinkedHashMultimap
				.create();

		Iterator<String> itr = baseEndPointsList.iterator();
		while (itr.hasNext()) {
			String str = itr.next();
			int start = str.indexOf('^');
			int end = str.indexOf('/');
			String base = "/" + str.substring(start + 1, end + 1);
			int flag = str.indexOf("include");
			str = str.substring(flag + 9);
			int fileflag = str.indexOf("\"");
			String fileName = str.substring(0, fileflag);
			int setter = fileName.indexOf(".");
			String fileDir = fileName.substring(0, setter);

			String targetPath = rootPath;
			targetPath = rootPath + "\\" + fileDir;
			File rootFile = new File(targetPath);

			try {
				boolean recursive = false;

				@SuppressWarnings("unchecked")
				Collection<File> files = FileUtils.listFiles(rootFile, null,
						recursive);

				for (Iterator<File> iterator = files.iterator(); iterator
						.hasNext();) {
					File file = (File) iterator.next();
					if (file.getName().equals("urls.py")) {

						FileInputStream fstream = new FileInputStream(file);
						DataInputStream input = new DataInputStream(fstream);
						BufferedReader br = new BufferedReader(
								new InputStreamReader(input));
						String strLine;

						while ((strLine = br.readLine()) != null) {

							if (strLine.contains("url(r'^$")) {

								pointsMap.put(base, null);

							} else if (strLine.contains("url(r'^")) {

								int tstart = strLine.indexOf("^");
								int tend = strLine.indexOf("$");
								if (tend == -1) {
									String temp = strLine.substring(tstart + 1,
											(strLine.lastIndexOf("/") + 1));
									pointsMap.put(base, temp);
								} else {
									String temp = strLine.substring(tstart + 1,
											tend);
									pointsMap.put(base, temp);
								}

							}

						}
						br.close();
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return pointsMap;
	}

}
