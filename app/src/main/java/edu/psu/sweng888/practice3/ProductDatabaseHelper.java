package edu.psu.sweng888.practice3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ProductDatabaseHelper extends SQLiteOpenHelper implements Serializable {

    // Database Name
    private static final String DB_NAME = "practice3.db";

    // Database version
    private static final int DB_VERSION = 1;

    //Column Names
    private static final String TABLE_NAME = "products";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "name";
    private static final String DESCRIPTION_COL = "description";
    private static final String SELLER_COL = "seller";
    private static final String PRICE_COL = "price";
    private static final String PICTURE_COL = "picture";

    // creating a constructor for our database handler.
    public ProductDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQLITE query to set column names
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT,"
                + SELLER_COL + " TEXT,"
                + PRICE_COL + " REAL,"
                + PICTURE_COL + " BLOB)";

        // execute query
        db.execSQL(query);
    }

    // this method is use to add new product table entry
    public void addNewProduct(String productName, String productDescription, String productSeller,
                              float productPrice, byte[] productPicture) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(NAME_COL, productName);
        values.put(DESCRIPTION_COL, productDescription);
        values.put(SELLER_COL, productSeller);
        values.put(PRICE_COL, productPrice);
        values.put(PICTURE_COL, productPicture);

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    public ArrayList<Product> queryAllCourses() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorProducts = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<Product>  productList = new ArrayList<Product>();

        if (cursorProducts.moveToFirst()) {
            do {
                productList.add(new Product(cursorProducts.getInt(0),
                        cursorProducts.getString(1), cursorProducts.getString(2),
                        cursorProducts.getString(3), cursorProducts.getFloat(4),
                        cursorProducts.getBlob(5)));
            } while(cursorProducts.moveToNext());
        }

        cursorProducts.close();
        return productList;
    }

    public byte[] queryProductPicture(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorProducts = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<Product>  productList = new ArrayList<Product>();

        if (cursorProducts.moveToFirst()) {
            do {
                productList.add(new Product(cursorProducts.getInt(0),
                        cursorProducts.getString(1), cursorProducts.getString(2),
                        cursorProducts.getString(3), cursorProducts.getFloat(4),
                        cursorProducts.getBlob(5)));
            } while(cursorProducts.moveToNext());
        }

        cursorProducts.close();
        return productList.stream().filter(p -> p.getId() == id).collect(Collectors.toList()).get(0)
                .getPicture();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void removeProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM products");
        db.close();
    }
    public void removeDuplicates() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM products WHERE rowid NOT IN (" +
                "SELECT MIN(rowid) FROM products GROUP BY name, description, seller)");
        db.close();
    }

}
