package com.denimgroup.threadfix.framework.engine.full;

import com.denimgroup.threadfix.framework.engine.FrameworkCalculator;
import com.denimgroup.threadfix.framework.engine.ProjectConfig;
import com.denimgroup.threadfix.framework.engine.cleaner.PathCleaner;
import com.denimgroup.threadfix.framework.engine.cleaner.PathCleanerFactory;
import com.denimgroup.threadfix.framework.engine.partial.PartialMapping;
import com.denimgroup.threadfix.framework.enums.FrameworkType;
import com.denimgroup.threadfix.framework.impl.django.DJANGOMappings;
import com.denimgroup.threadfix.framework.impl.jsp.JSPMappings;
import com.denimgroup.threadfix.framework.impl.spring.SpringControllerMappings;
import com.denimgroup.threadfix.framework.util.SanitizedLogger;


import java.io.File;

import java.util.ArrayList;
import java.util.List;

public class EndpointDatabaseFactory {
	
	private static final SanitizedLogger log = new SanitizedLogger("EndpointDatabaseFactory");

        public static EndpointDatabase getDatabase( ProjectConfig projectConfig) {

        EndpointDatabase database = null;

        if (projectConfig.getRootFile() != null) {
            if (projectConfig.getFrameworkType() != null &&
                    projectConfig.getFrameworkType() != FrameworkType.DETECT) {
                database = getDatabase(projectConfig.getRootFile(), projectConfig.getFrameworkType());
            } else {
                database = getDatabase(projectConfig.getRootFile());
            }
        }

        return database;
    }

	
    public static EndpointDatabase getDatabase( File rootFile) {
		FrameworkType type = FrameworkCalculator.getType(rootFile);
        if(type==FrameworkType.getFrameworkType("DJANGO")){
            PathCleaner cleaner=null;
            return getDatabase(rootFile,type,cleaner);
        }
        else
		return getDatabase(rootFile, type);
	}
	
	
    public static EndpointDatabase getDatabase( File rootFile, List<PartialMapping> partialMappings) {
		FrameworkType type = FrameworkCalculator.getType(rootFile);
		
		return getDatabase(rootFile, type, partialMappings);
	}

	
    public static EndpointDatabase getDatabase( File rootFile,  FrameworkType frameworkType) {
		return getDatabase(rootFile, frameworkType, new ArrayList<PartialMapping>());
	}
	
	
    public static EndpointDatabase getDatabase( File rootFile,  FrameworkType frameworkType, List<PartialMapping> partialMappings) {
		PathCleaner cleaner = PathCleanerFactory.getPathCleaner(frameworkType, partialMappings);
		
		return getDatabase(rootFile, frameworkType, cleaner);
	}
	
	
    public static EndpointDatabase getDatabase( File rootFile,  FrameworkType frameworkType, PathCleaner cleaner) {
		EndpointGenerator generator = null;
		
		switch (frameworkType) {
			case JSP:        generator = new JSPMappings(rootFile);              break;
			case SPRING_MVC: generator = new SpringControllerMappings(rootFile); break;
           case DJANGO:     generator = new DJANGOMappings(rootFile);
			default:
		}
		
		log.info("Returning database with generator: " + generator);

		if (generator == null) {
            return null;
        } else {
		    return new GeneratorBasedEndpointDatabase(generator, cleaner, frameworkType);
        }
	}
	
}
