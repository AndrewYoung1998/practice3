package edu.psu.sweng888.practice3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private ProductAdapter adapter;
    private List<Product> selectedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Button btnSendEmail = findViewById(R.id.btnSendEmail);

        // Retrieve the selected products list from the intent (make sure key matches)
        selectedProducts = getIntent().getParcelableArrayListExtra("products");

        // Check if the list is null or empty
        if (selectedProducts == null || selectedProducts.isEmpty()) {
            // Handle the case when no products are passed or there's an error
            Toast.makeText(this, "No products selected", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity as there's no data to display
            return;
        }

        // Setup RecyclerView for selected products
        adapter = new ProductAdapter(selectedProducts, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Handle "Send Email" button click
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailWithProducts();
            }
        });
    }

    private void sendEmailWithProducts() {
        StringBuilder emailBody = new StringBuilder();
        ArrayList<Uri> imageUris = new ArrayList<>();

        // Convert drawable resources to Bitmaps and save as files
        int[] drawableIds = {R.drawable.nintendo_switch, R.drawable.ps5, R.drawable.xbox_series_x};

        // Iterate over products and build email body
        for (Product product : selectedProducts) {
            emailBody.append("Product Name: ").append(product.getName()).append("\n");
            emailBody.append("Description: ").append(product.getDescription()).append("\n");
            emailBody.append("Seller: ").append(product.getSeller()).append("\n");
            emailBody.append("Price: $").append(product.getPrice()).append("\n\n");
        }

        // Attach images (with fixed drawable IDs)
        for (int drawableId : drawableIds) {
            Bitmap bitmap = getBitmapFromDrawable(drawableId);  // Convert drawable to Bitmap
            if (bitmap != null) {
                // Save the Bitmap as a file and get its Uri
                Uri imageUri = saveBitmapToFile(bitmap, getResources().getResourceEntryName(drawableId) + ".png");
                if (imageUri != null) {
                    imageUris.add(imageUri);  // Add Uri to the list of attachments
                }
            }
        }

        // Prepare the email intent
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sweng888mobileapps@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Selected Products");
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody.toString());  // Add the email body text

        // Attach the image files
        if (!imageUris.isEmpty()) {
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);  // Grant permission to read the URIs
        }

        // Send the email
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
            Toast.makeText(SecondActivity.this, "Email sent!", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SecondActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


    // Convert drawable to Bitmap
    private Bitmap getBitmapFromDrawable(int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(this, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            assert drawable != null;
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    // Save the Bitmap as a file and return the Uri using FileProvider
    private Uri saveBitmapToFile(Bitmap bitmap, String fileName) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            // Return the URI of the saved file using FileProvider
            return FileProvider.getUriForFile(this, "edu.psu.sweng888.practice3.provider", file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
