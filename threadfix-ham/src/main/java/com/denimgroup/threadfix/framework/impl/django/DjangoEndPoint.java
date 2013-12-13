package com.denimgroup.threadfix.framework.impl.django;


import java.util.Set;


import com.denimgroup.threadfix.framework.engine.AbstractEndpoint;

public class DjangoEndPoint extends AbstractEndpoint{


    private final String urlPath;


    private final Set<String> methods;


    private final Set<String> parameterMap;



    public DjangoEndPoint(String urlPath,  Set<String> allParameters, Set<String> methods){

        this.urlPath=urlPath;
        this.parameterMap=allParameters;
        this.methods=methods;


    }


    @Override
    public Set<String> getParameters() {
        // TODO Auto-generated method stub

        return parameterMap;
    }

    @Override
    public Set<String> getHttpMethods() {
        // TODO Auto-generated method stub
        return methods;
    }

    @Override
    public String getUrlPath() {
        // TODO Auto-generated method stub
        return urlPath;
    }

    @Override
    public String getFilePath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getStartingLineNumber() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getLineNumberForParameter(String parameter) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean matchesLineNumber(int lineNumber) {
        // TODO Auto-generated method stub
        return false;
    }

}
