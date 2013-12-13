package com.denimgroup.threadfix.framework.impl.django;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

//import org.apache.commons.io.FileUtils;

import com.denimgroup.threadfix.framework.beans.DefaultEndpoint;
import com.denimgroup.threadfix.framework.engine.Endpoint;
import com.denimgroup.threadfix.framework.engine.EndpointGenerator;
import com.google.common.collect.LinkedHashMultimap;

public class DJANGOMappings implements EndpointGenerator {

	LinkedHashMultimap<Object, Object> pointsMap = LinkedHashMultimap.create();
	LinkedHashMultimap<Object, Object> modelMap = LinkedHashMultimap.create();
	HashMap<Object, Object> urlModelMap = new HashMap<Object, Object>();
	ArrayList<String> endPointsList = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public DJANGOMappings(File rootFile) {

		String rootPath = rootFile.getAbsolutePath();

		String fileName = "urls.py";

		try {

			boolean recursive = true;

			Collection<File> files = FileUtils.listFiles(rootFile, null,
					recursive);
			ArrayList<String> baseEndPointsList = new ArrayList<String>();
			ArrayList<String> constructors = new ArrayList<String>();
			ArrayList<String> parameterizedEndPoints = new ArrayList<String>();

			for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
				File file = iterator.next();
				ArrayList<String> rawPointsList = new ArrayList<String>();

				if (file.getName().equals(fileName)) {

					FileInputStream fstream = new FileInputStream(file);
					DataInputStream input = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(input));
					String strLine;

					while ((strLine = br.readLine()) != null) {

						if (strLine.contains("url(r")
								&& (strLine.contains("include"))
								&& !strLine.contains("#")) {

							baseEndPointsList.add(strLine);
							int start = strLine.indexOf('^');
							int end = strLine.indexOf('/');
							String base = "/"
									+ strLine.substring(start + 1, end + 1);
							constructors.add(base);
						}
						if (strLine.contains("url(r'^$")
								&& (!strLine.contains("#"))) {
							String base = "/";
							rawPointsList.add(base);

						}
					}

					rawPointsList.addAll(baseEndPointsList);
					rawPointsList = rmDuplicates(rawPointsList);

					br.close();
				}
			}

			Constructendpoints endpointsbuilder = new Constructendpoints();
			pointsMap = endpointsbuilder.constructEndPoints(baseEndPointsList,
					rootPath);
			endPointsList = generateEndPoints(pointsMap, constructors);

			Iterator<String> printer = endPointsList.iterator();
			while (printer.hasNext()) {
				String disp = printer.next();
				if (disp.contains("?P"))
					parameterizedEndPoints.add(disp);
			}
            
			ModelScan modelmapper = new ModelScan();
			modelMap = modelmapper.modelScan(constructors,
					parameterizedEndPoints, rootFile);

			Url2Model urlmapper = new Url2Model();
			urlModelMap = urlmapper.url2model(constructors,
					parameterizedEndPoints, rootFile);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static ArrayList<String> rmDuplicates(ArrayList<String> rawPointsList) {

		Set<String> rmDup = new LinkedHashSet<String>();
		rmDup.addAll(rawPointsList);
		rawPointsList.clear();
		rawPointsList.addAll(rmDup);
		return rawPointsList;

	}

	public static ArrayList<String> generateEndPoints(
			LinkedHashMultimap<Object, Object> pointsMap,
			ArrayList<String> constructors) {

		ArrayList<String> endPointsList = new ArrayList<String>();
		Iterator<String> scan = constructors.iterator();
		while (scan.hasNext()) {
			String str = scan.next();
			Set<Object> results = pointsMap.get(str);
			Iterator<Object> printer = results.iterator();
			while (printer.hasNext()) {
				String base = str;
				String append = (String) printer.next();
				if (append != null) {
					base = str + append;
					endPointsList.add(base);

				} else if (append == null) {
					endPointsList.add(base);
				}

			}
		}
		return endPointsList;
	}

	@Override
	public List<Endpoint> generateEndpoints() {
		// TODO Auto-generated method stub
		List<Endpoint> endpoints = new ArrayList<>();

		Iterator<String> itr = endPointsList.iterator();
		while (itr.hasNext()) {

			String urlPath = itr.next();
			Set<String> allParameters = new HashSet<>();
			String modelName = (String) urlModelMap.get(urlPath);
			Set<Object> params = modelMap.get(modelName);
			Iterator<Object> pars = params.iterator();
			while (pars.hasNext()) {
				allParameters.add((String) pars.next());
			}
			endpoints.add(new DefaultEndpoint(urlPath, allParameters,
					new HashSet<String>(Arrays.asList("GET", "POST"))));

		}

		return endpoints;
	}

}
