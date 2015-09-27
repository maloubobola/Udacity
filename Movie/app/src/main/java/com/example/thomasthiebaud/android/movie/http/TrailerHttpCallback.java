package com.example.thomasthiebaud.android.movie.http;

import com.example.thomasthiebaud.android.movie.model.contract.APIContract;
import com.example.thomasthiebaud.android.movie.model.item.TrailerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasthiebaud on 27/09/15.
 */
public abstract class TrailerHttpCallback implements HttpResponse<TrailerItem>, JsonResponse {
    @Override
    public void onSuccess(JSONObject object) {
        this.onSuccess(fromJson(object));
    }

    @Override
    public List<TrailerItem> fromJson(JSONObject json) {
        List<TrailerItem> trailers = new ArrayList<>();
        JSONArray results;
        try {
            results = json.getJSONArray(APIContract.JSON_RESULTS);
            for(int i=0; i<results.length(); i++) {
                JSONObject jsonTrailer = results.getJSONObject(i);
                TrailerItem trailerItem = new TrailerItem();
                trailerItem.setId(jsonTrailer.getString(APIContract.JSON_ID));
                trailerItem.setName(jsonTrailer.getString(APIContract.JSON_NAME));
                trailerItem.setKey(jsonTrailer.getString(APIContract.JSON_KEY));
                trailers.add(trailerItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }

}
