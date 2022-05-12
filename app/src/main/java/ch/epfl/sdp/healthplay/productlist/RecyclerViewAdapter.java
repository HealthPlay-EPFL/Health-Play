package ch.epfl.sdp.healthplay.productlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import ch.epfl.sdp.healthplay.BarcodeInformationActivity;
import ch.epfl.sdp.healthplay.ProductInfoActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.model.Product;
import ch.epfl.sdp.healthplay.model.ProductInfoClient;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<String> mProducts;
    private List<String> mDates;
    private Context mContext;

    public RecyclerViewAdapter(Context mContext, List<String> mProducts, List<String> mDates) {
        this.mProducts = mProducts;
        this.mDates = mDates;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        AtomicReference<Product> product = new AtomicReference<>(null);
        Thread t = new Thread(() -> {
            try {
                Product.of(new ProductInfoClient(mProducts.get(position)).getInfo())
                        .ifPresent(product::set);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Glide.with(mContext)
                .asBitmap()
                .load(product.get().getImageURL())
                .into(holder.image);

        holder.productDate.setText(mDates.get(position));

        String name = product.get().getGenericName().isEmpty() ?
                product.get().getName() :
                product.get().getGenericName();

        holder.productName.setText(name);

        holder.productListLayout.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: clicked");
            Intent intent = new Intent(mContext, BarcodeInformationActivity.class);
            String message = product.get().getCode();
            intent.putExtra(BarcodeInformationActivity.EXTRA_MESSAGE, message);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView productDate;
        TextView productName;
        RelativeLayout productListLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.productListImage);
            productDate = itemView.findViewById(R.id.productListDate);
            productName = itemView.findViewById(R.id.productListName);
            productListLayout = itemView.findViewById(R.id.productListLayout);
        }
    }
}
