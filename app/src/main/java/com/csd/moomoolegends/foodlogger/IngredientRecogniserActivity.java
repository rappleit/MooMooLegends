package com.csd.moomoolegends.foodlogger;
import com.csd.moomoolegends.BuildConfig;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.csd.moomoolegends.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class IngredientRecogniserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_recogniser);
        String photoPath = getIntent().getStringExtra("photo_path");
        if (photoPath != null) {
            compressAndUploadImage(photoPath);
        }
    }
    private void compressAndUploadImage(String photoPath) {
        File originalFile = new File(photoPath);

        if (!originalFile.exists()) {
            Log.e("Upload", "File does not exist");
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        options.inSampleSize = calculateInSampleSize(options, 1024, 768);
        options.inJustDecodeBounds = false;
        Bitmap compressedBitmap = BitmapFactory.decodeFile(photoPath, options);

        File compressedFile = new File(getExternalFilesDir(null), "compressed_image.jpg");
        try (FileOutputStream out = new FileOutputStream(compressedFile)) {
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            out.flush();
            Log.d("Upload", "Compressed image saved successfully.");
        } catch (IOException e) {
            Log.e("Upload", "Failed to save compressed image", e);
            return;
        }

        if (compressedFile.length() >= 2 * 1024 * 1024) {
            Log.e("Upload", "Compressed file is still too large");
            return;
        }

        uploadImage(compressedFile);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void uploadImage(File file) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://vision.foodvisor.io/api/1.0/en/analysis/";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(file, MediaType.parse("image/jpeg")))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Api-Key " + BuildConfig.FOODVISOR_API_KEY)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Upload", "Failed to upload file: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("Upload", "Server responded with: " + response.code() + " and message: " + response.body().string());
                    startIngredientsListActivity(null);
                } else {
                    String responseData = response.body().string();
                    try {
                        JsonObject jsonResponse = JsonParser.parseString(responseData).getAsJsonObject();
                        JsonObject filteredResponse = new JsonObject();
                        filteredResponse.addProperty("analysis_id", jsonResponse.get("analysis_id").getAsString());

                        JsonArray itemsArray = jsonResponse.getAsJsonArray("items");
                        JsonArray filteredItemsArray = new JsonArray();

                        // Iterate through each item and filter based on confidence level
                        for (int i = 0; i < itemsArray.size(); i++) {
                            JsonArray foodItems = itemsArray.get(i).getAsJsonObject().getAsJsonArray("food");
                            JsonArray filteredFoodItems = new JsonArray();
                            for (int j = 0; j < foodItems.size(); j++) {
                                JsonObject foodItem = foodItems.get(j).getAsJsonObject();
                                double confidence = foodItem.get("confidence").getAsDouble();
                                if (confidence > 0.4) {
                                    filteredFoodItems.add(foodItem);
                                }
                            }
                            if (filteredFoodItems.size() > 0) {
                                JsonObject newItem = new JsonObject();
                                newItem.add("food", filteredFoodItems);
                                filteredItemsArray.add(newItem);
                            }
                        }

                        filteredResponse.add("items", filteredItemsArray);
                        Log.i("Upload", "Successfully uploaded and parsed: " + filteredResponse.toString());
                        startIngredientsListActivity(filteredResponse.toString());

                    } catch (Exception e) {
                        Log.e("Upload", "Error parsing JSON", e);
                        startIngredientsListActivity(null);
                    }
                }
            }

        });
    }
    private void startIngredientsListActivity(String responseData) {
        Intent intent = new Intent(IngredientRecogniserActivity.this, IngredientsListActivity.class);
        if (responseData != null) {
            intent.putExtra("response_data", responseData);
        }
        intent.putExtra("photo_path", getIntent().getStringExtra("photo_path"));
        startActivity(intent);
        finish();

    }
}
