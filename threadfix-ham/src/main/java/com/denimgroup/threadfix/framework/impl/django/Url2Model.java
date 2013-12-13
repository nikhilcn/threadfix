package com.denimgroup.threadfix.framework.impl.django;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Url2Model {

    public HashMap<Object, Object> url2model(ArrayList<String> constructors,
                                             ArrayList<String> parameterizedEndPoints, File rootFile) {
        // TODO Auto-generated method stub
        HashMap<Object, Object> urlModelMap = new HashMap<Object, Object>();
        Iterator<String> iterCons = constructors.iterator();
        ArrayList<String> imports = new ArrayList<String>();

        while (iterCons.hasNext()) {
            String ref = (String) iterCons.next();
            Iterator<String> iterParEndPoints = parameterizedEndPoints
                    .iterator();
            while (iterParEndPoints.hasNext()) {

                String nextVal = (String) iterParEndPoints.next();
                String key4Map = nextVal;
                String rootPath = rootFile.getAbsolutePath();
                int start = ref.indexOf("/");
                int end = ref.lastIndexOf("/");
                if (nextVal.subSequence(start + 1, end).equals(
                        ref.subSequence(start + 1, end))) {
                    String fileDir = rootPath + "\\"
                            + ref.substring(start + 1, end);
                    String urlsPath = fileDir + "\\urls.py";
                    String viewsPath = fileDir + "\\views.py";

                    try {

                        FileReader urlsReader = new FileReader(urlsPath);
                        BufferedReader urlsBuffer = new BufferedReader(
                                urlsReader);
                        String fileContent;
                        while ((fileContent = urlsBuffer.readLine()) != null) {
                            String filter = nextVal.substring(end + 1);
                            filter = filter + "$";

                            if (fileContent.contains(filter)) {

                                String pivot = "view";
                                String extract = fileContent
                                        .substring((fileContent.indexOf(pivot) + 6));
                                String viewName = extract.substring(0,
                                        (extract.indexOf(pivot) + 4));
                                if (viewName.contains(".")) {
                                    viewName = viewName.substring(0,
                                            viewName.indexOf("."));
                                }
                                FileReader viewReader = new FileReader(
                                        viewsPath);
                                BufferedReader viewBuffer = new BufferedReader(
                                        viewReader);
                                String viewContent;
                                while ((viewContent = viewBuffer.readLine()) != null) {

                                    if (viewContent.contains("import"))
                                        imports.add(viewContent);
                                    else if (viewContent.contains(viewName)) {
                                        String fetchModel;
                                        while ((fetchModel = viewBuffer
                                                .readLine()) != null) {

                                            if (fetchModel.contains("model")) {
                                                String modelName = fetchModel
                                                        .substring(
                                                                (fetchModel
                                                                        .indexOf("=") + 1),
                                                                (fetchModel
                                                                        .length()));
                                                urlModelMap.put(key4Map,
                                                        modelName);

                                            }
                                        }
                                    }
                                }
                                viewBuffer.close();
                            }
                        }
                        urlsBuffer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return urlModelMap;
    }

}
