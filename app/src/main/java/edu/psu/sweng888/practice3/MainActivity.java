package edu.psu.sweng888.practice3;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "Practice3Main";
    private ProductDatabaseHelper db;
    private List<Product> mProductList;
    private RecyclerView.Adapter mAdapter;

    public static String getLogTag() {
        return LOG_TAG;
    }

    public static void setLogTag(String logTag) {
        LOG_TAG = logTag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //configure database and insert our product entries
        initDB();

        RecyclerView mListView = (RecyclerView) findViewById(R.id.recyclerView);
        mProductList = db.queryAllCourses();

        mAdapter = new ProductAdapter(mProductList, null);
        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(this));

        //Configure Proceed Button
        Button proceedButton = findViewById(R.id.proceedButton);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enoughItemsSelected()) {
                    //Proceed to next activity
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    // Filter selected products and pass them to the next activity
                    ArrayList<Product> selectedProducts = mProductList.stream()
                            .filter(e -> ((ProductAdapter) mAdapter)
                                    .getProductSelectedMap().get(e.getId())).collect(Collectors.toCollection(ArrayList::new));
                    if (selectedProducts.isEmpty()) {
                        Toast.makeText(MainActivity.this, "No products selected!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent.putParcelableArrayListExtra("products", selectedProducts);
                    startActivity(intent);
                } else {
                    //Display Error Snackbar
                    Snackbar snackbar = Snackbar.make(v, "Please select 3 or more products!",
                            Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private boolean enoughItemsSelected() {
        return ((ProductAdapter) mAdapter).getSelectedItemCount() >= 3;
    }

    private void initDB() {
        db = new ProductDatabaseHelper(getApplicationContext());
        db.removeProducts();
        // Check if the database already contains products
            // Add new products only if the table is empty
            db.addNewProduct("Nintendo Switch", "A fun gaming console",
                    "Nintendo", 299f,
                    bitmapToByteArray(drawableToBitmap(AppCompatResources.getDrawable(this,
                            R.drawable.switch_bg))));

            db.addNewProduct("Playstation 5", "A powerful gaming console",
                    "Sony", 499f,
                    bitmapToByteArray(drawableToBitmap(AppCompatResources.getDrawable(this,
                            R.drawable.playstation_background))));

            db.addNewProduct("Xbox Series X", "A gaming console",
                    "Microsoft", 499f,
                    bitmapToByteArray(drawableToBitmap(AppCompatResources.getDrawable(this,
                            R.drawable.playstation_background))));
        // Remove duplicates
        db.removeDuplicates();
    }

    private static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}

