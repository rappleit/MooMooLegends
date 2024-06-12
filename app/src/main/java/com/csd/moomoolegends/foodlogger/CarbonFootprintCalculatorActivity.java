package com.csd.moomoolegends.foodlogger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.csd.moomoolegends.BuildConfig;
import com.csd.moomoolegends.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.*;

public class CarbonFootprintCalculatorActivity extends AppCompatActivity {
    private static final String API_KEY = BuildConfig.OPENAIASSISTANT_API_KEY;
    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carbon_footprint_calculator);
        Intent intent = getIntent();
        ArrayList<String> tuplesList = intent.getStringArrayListExtra("ingredientTuples");
        String ingredientsString = "[" + String.join(", ", tuplesList) + "]";
        Log.d("ingredients", ingredientsString);
        sendRequestToAPI(ingredientsString);
    }
    private void sendRequestToAPI(String userPrompt) {
        String jsonBody = "{\"assistant_id\": \"asst_SoW0CCyViLsTp1H93RKOGMe9\", \"thread\": {\"messages\": [{\"role\": \"user\", \"content\": \"" + userPrompt + "\"}]}}";
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/threads/runs")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("OpenAI-Beta", "assistants=v2")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CarbonLogger1", "Failed to send request: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("CarbonLogger1", "Server responded with: " + response.code() + " and message: " + response.body().string());
                } else {
                    String responseData = response.body().string();
                    String threadId = parseThreadId(responseData);
                    String runId = parseRunId(responseData);
                    if (threadId != null) {
                        pollForThreadCompletion(threadId, runId);
                    }
                }
            }
        });
    }

    private String parseThreadId(String jsonResponse) {
        try {
            JsonObject responseJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return responseJson.get("thread_id").getAsString();
        } catch (Exception e) {
            Log.e("CarbonLogger2", "Error parsing JSON for thread ID", e);
            return null;
        }
    }

    public String parseRunId(String jsonResponse) {
        try {
            JsonObject responseJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
            String runId = responseJson.get("id").getAsString();
            Log.d("CarbonLogger7", "Extracted Run ID: " + runId);
            return runId;
        } catch (Exception e) {
            Log.e("CarbonLogger7", "Error parsing JSON for run ID", e);
            return null;
        }
    }

    private void pollForThreadCompletion(final String threadId, final String runId) {

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/threads/" + threadId + "/runs/" + runId)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("OpenAI-Beta", "assistants=v2")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CarbonLogger3", "Failed to check status: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("CarbonLogger3", "Server responded with: " + response.code() + " and message: " + response.body().string());
                } else {
                    String responseData = response.body().string();
                    if (isThreadComplete(responseData)) {
                        getThreadMessages(threadId);
                    } else {
                        handler.postDelayed(() -> pollForThreadCompletion(threadId, runId), 5000);
                    }
                }
            }
        });
    }

    private boolean isThreadComplete(String jsonResponse) {
        try {
            JsonObject responseJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
            String status = responseJson.get("status").getAsString();
            return "completed".equals(status);
        } catch (Exception e) {
            Log.e("CarbonLogger4", "Error parsing JSON for status", e);
            return false;
        }
    }

    private void getThreadMessages(String threadId) {

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/threads/" + threadId + "/messages")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("OpenAI-Beta", "assistants=v2")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CarbonLogger5", "Failed to retrieve messages: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("CarbonLogger5", "Server responded with: " + response.code() + " and message: " + response.body().string());
                } else {
                    String responseData = response.body().string();
                    Log.i("CarbonLogger5", "Messages retrieved successfully: " + responseData);
                    String carbonFootprintJson = extractCarbonFootprintJson(responseData);
                    Log.d("CarbonLogger8", carbonFootprintJson);
                    if (carbonFootprintJson != null) {
                        Intent displayIntent = new Intent(CarbonFootprintCalculatorActivity.this, LoggerResultsActivity.class);
                        displayIntent.putExtra("carbonData", carbonFootprintJson);
                        startActivity(displayIntent);
                    }
                }
            }
        });
    }
    private String extractCarbonFootprintJson(String jsonResponse) {
        try {
            JsonObject responseJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray dataArray = responseJson.getAsJsonArray("data");
            for (JsonElement element : dataArray) {
                JsonObject dataObject = element.getAsJsonObject();
                if ("assistant".equals(dataObject.get("role").getAsString())) {
                    JsonArray contentArray = dataObject.getAsJsonArray("content");
                    for (JsonElement contentElement : contentArray) {
                        JsonObject contentObject = contentElement.getAsJsonObject();
                        JsonObject textObject = contentObject.getAsJsonObject("text");
                        return textObject.get("value").getAsString();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("CarbonLogger6", "Error extracting carbon footprint JSON", e);
        }
        return null;
    }

}