package com.example.android_mas.products;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_mas.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    CircleImageView product_tv;
    ImageView imgproduct;
    TextView productname;
    TextView productprice;
    TextView nameprofile;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        product_tv = itemView.findViewById(R.id.product_tv);
        imgproduct = itemView.findViewById(R.id.imgproduct);
        productname = itemView.findViewById(R.id.productname);
        productprice = itemView.findViewById(R.id.productprice);
        nameprofile = itemView.findViewById(R.id.nameprofile);
    }
}
