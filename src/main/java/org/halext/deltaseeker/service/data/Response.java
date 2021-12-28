package org.halext.deltaseeker.service.data;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Response {
    
    public class ResponseDecoder implements Decoder.Text<Response> {
        @Override
        public void init(EndpointConfig ec) { /* do nothing */ }
        @Override
        public void destroy() { /* also do nothing */ }
        @Override
        public Response decode(String response) throws DecodeException {
              return new Response(response);
        }
        @Override
        public boolean willDecode(String string) {
           return string.startsWith("{");
        }
    }

    private JSONObject responseObject;

    public Response(String response) {
        JSONParser parser = new JSONParser();
        try {
            this.responseObject = (JSONObject) parser.parse(response);
            if ( !responseObject.get("notify").equals(null) ) {
                this.responseType = ResponseType.NOTIFY;
            } else if ( !responseObject.get("data").equals(null) ) {
                this.responseType = ResponseType.DATA;
            } else {
                this.responseType = ResponseType.UNKNOWN;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // ignore 
        }
    }


    public enum ResponseType {
        NOTIFY,
        DATA,
        UNKNOWN
    }

    ResponseType responseType;

    public void setResponseType( ResponseType rt ) {
        this.responseType = rt;
    }

    public ResponseType getResponseType() {
        return this.responseType;
    }


}
