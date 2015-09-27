package com.example.thomasthiebaud.android.movie.controller.data.http;

import com.example.thomasthiebaud.android.movie.model.contract.APIContract;
import com.example.thomasthiebaud.android.movie.model.item.ReviewItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomasthiebaud on 27/09/15.
 */
public abstract class ReviewHttpCallback implements HttpResponse<ReviewItem>, JsonResponse {

    @Override
    public void onSuccess(JSONObject object) {
        this.onSuccess(fromJson(object));
    }

    @Override
    public List<ReviewItem> fromJson(JSONObject json) {
        List<ReviewItem> reviews = new ArrayList<>();
        JSONArray results;
        try {
            results = json.getJSONArray(APIContract.JSON_RESULTS);
            for(int i=0; i<results.length(); i++) {
                JSONObject jsonReview = results.getJSONObject(i);
                ReviewItem reviewItem = new ReviewItem();
                reviewItem.setId(jsonReview.getString(APIContract.JSON_ID));
                reviewItem.setAuthor(jsonReview.getString(APIContract.JSON_AUTHOR));
                reviewItem.setContent(jsonReview.getString(APIContract.JSON_CONTENT));
                reviewItem.setUrl(jsonReview.getString((APIContract.JSON_URL)));
                reviews.add(reviewItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
