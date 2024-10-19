package edu.psu.sweng888.practice3;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private final Map<Integer, Boolean> mProductSelectedMap = new HashMap<>();
    private final List<Product> mProductList;

    public ProductAdapter(List<Product> productList, Object o) {
        mProductList = productList;
        for (int i = 0; i < mProductList.size(); i++) {
            mProductSelectedMap.put(mProductList.get(i).getId(), false);
        }
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Product product = mProductList.get(position);  // This uses the product list

        holder.textView.setText(product.getName());

        // Set background color based on selection state
        holder.view.setBackgroundColor(Boolean.TRUE.equals(mProductSelectedMap.get(product.getId()))
                ? Color.CYAN
                : Color.WHITE);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle the selection state for the product
                mProductSelectedMap.put(product.getId(), Boolean.FALSE.equals(mProductSelectedMap.get(product.getId())));

                // Update background color based on selection state
                holder.view.setBackgroundColor(Boolean.TRUE.equals(mProductSelectedMap.get(product.getId()))
                        ? Color.CYAN
                        : Color.WHITE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProductList.size();  // Use the size of mProductList, not mProductSelectedMap
    }

    public int getSelectedItemCount() {
        return (int) mProductSelectedMap.values().stream().filter(b -> b).count();
    }

    public Map<Integer, Boolean> getProductSelectedMap() {
        return mProductSelectedMap;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final TextView textView;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.textView = (TextView) view.findViewById(R.id.text_view);
        }
    }
}
