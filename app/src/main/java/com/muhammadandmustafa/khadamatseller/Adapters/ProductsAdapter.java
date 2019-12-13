package com.muhammadandmustafa.khadamatseller.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.muhammadandmustafa.khadamatseller.Models.Product;
import com.muhammadandmustafa.khadamatseller.R;

import java.util.List;

public class ProductsAdapter extends ArrayAdapter<Product> {

    private Product ProductBody;

    public ProductsAdapter(Context context, int resource, List<Product> sampleArrayList) {
        super(context, resource, sampleArrayList);
    }

    @Nullable
    @Override
    public Product getItem(int position) {
        //latest swaps on top of the list
        return super.getItem(getCount() - position - 1);
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_list_item, parent, false);
            Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            convertView.startAnimation(animation);
            lastPosition = position;
        }

        final Context context = convertView.getContext();
        ImageView product_image = convertView.findViewById(R.id.productImgItem);
        TextView product_description = convertView.findViewById(R.id.productDescriptionItem);
        TextView product_name = convertView.findViewById(R.id.productNameItem);
        TextView product_price = convertView.findViewById(R.id.productPriceItem);

        ProductBody = getItem(position);

        final ProgressBar progressBarProductListItem = convertView.findViewById(R.id.progressBarProductListItem);

        if (ProductBody != null) {

            product_name.setText(ProductBody.getName());
            product_price.setText(ProductBody.getPrice());
            product_description.setText(ProductBody.getDescription());
            if (ProductBody.getImgURL() != null) {
                progressBarProductListItem.setVisibility(View.VISIBLE);
                Glide.with(product_image.getContext())
                        .load(ProductBody.getImgURL())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                progressBarProductListItem.setVisibility(View.GONE);
                                return false;
                            }

                        })
                        .into(product_image);
            } else {
                progressBarProductListItem.setVisibility(View.GONE);
                // set the swapper Image to default if no image provided
                Resources resources = context.getResources();
                Drawable photoUrl = resources.getDrawable(R.drawable.noimage);
                product_image.setImageDrawable(photoUrl);
            }
//            userId = swapBody.getSwapperID();
        }

        return convertView;

    }

}
