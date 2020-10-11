package org.ufam.gather4u.interfaces;

import org.json.JSONObject;
import org.ufam.gather4u.application.Constants;

public interface CustomHttpResponse {
    void OnHttpResponse(JSONObject response, Constants.HttpMessageType flag);
}
