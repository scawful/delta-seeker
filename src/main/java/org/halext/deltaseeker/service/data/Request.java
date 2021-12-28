package org.halext.deltaseeker.service.data;

import org.json.simple.JSONObject;

public class Request {
    
    JSONObject requestJSONObject;
    String queryJSONString;
    String requestType;

    public Request(JSONObject jo, String query, String type) {
        this.requestJSONObject = jo;
        this.queryJSONString = query;
        this.requestType = type;
    }

    public String getRequestType() {
        return this.requestType;
    }

    public String getQueryString() {
        return this.queryJSONString;
    }
}
