package com.colorfit.coloring.colory.family.utils;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class MyHttpClient {
    private int timeoutConnection = 6000;
    private HttpParams httpParameters;
    private int timeoutSocket = 6000;

    public MyHttpClient() {
        httpParameters = new BasicHttpParams();// Set the timeout in
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);// Set the default socket timeout
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
    }
}