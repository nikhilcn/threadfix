package com.denimgroup.threadfix.framework.impl.django;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.LinkedHashMultimap;

public class ConstructendPoints {

	public LinkedHashMultimap<Object, Object> constructEndPoints(
			ArrayList<String> baseEndPointsList, String rootPath) {

		LinkedHashMultimap<Object, Object> pointsMap = LinkedHashMultimap
				.create();

        for (String str : baseEndPointsList) {

			int start = str.indexOf('^');
			int end = str.indexOf(',');
			String base = "/" + str.substring(start + 1, end - 1);
			int flag = str.indexOf("include");
			str = str.substring(flag + 9);
			int fileflag = str.indexOf("urls") - 1;

			String fileName = str.substring(0, fileflag);

			fileName = fileName.replace('.', '\\');
			String fileDir = fileName;

			String targetPath = rootPath;
			targetPath = rootPath + "\\" + fileDir;
			File rootFile = new File(targetPath);

			pointsMap.putAll(collectPoints(rootFile, base));
		}
		return pointsMap;

	}

	public LinkedHashMultimap<Object, Object> collectPoints(File path,
			String base) {

		LinkedHashMultimap<Object, Object> pointsMap = LinkedHashMultimap
				.create();

		try {

			boolean recursive = false;

			Collection<File> files = FileUtils.listFiles(path, null, recursive);

			for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				if (file.getName().equals("urls.txt")) {

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

		return pointsMap;
	}

	public LinkedHashMultimap<Object, Object> collectPoints(
			LinkedHashMultimap<Object, Object> sampler) {

		LinkedHashMultimap<Object, Object> pointsMap = LinkedHashMultimap
				.create();
		Set<Object> absPathlist = new HashSet<Object>();
		absPathlist = sampler.keySet();

		try {

			@SuppressWarnings("rawtypes")
			Iterator itr = absPathlist.iterator();
			while (itr.hasNext())

			{
				String pathval = itr.next().toString();
				File path = new File(pathval);

				FileInputStream fstream = new FileInputStream(path);
				DataInputStream input = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						input));
				String strLine;

				while ((strLine = br.readLine()) != null) {

					if (strLine.contains("url(r'^")) {

						int tstart = strLine.indexOf("^");
						int tend = strLine.indexOf("$");
						if (tend == -1) {
							String temp = strLine.substring(tstart + 1,
									(strLine.lastIndexOf("/") + 1));
							pointsMap.put("", temp);
						} else {
							String temp = strLine.substring(tstart + 1, tend);
							pointsMap.put("unmoduled", temp);
						}

					}

				}
				br.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pointsMap;
	}

}
