package com.cybex.componentservice.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.cybex.componentservice.R;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.utils.SizeUtil;

import java.security.MessageDigest;
import java.util.List;

public class TokenIconAdapter extends RecyclerView.Adapter<TokenIconAdapter.TokenHolder> {


    private final List<TokenBean> tokens;
    private int height;
    private int width;
    private int miniWidth;

    public TokenIconAdapter(
            List<TokenBean> tokens) {
        this.tokens=tokens;
        height=SizeUtil.dp2px(27);
        width=SizeUtil.dp2px(27);
        miniWidth=SizeUtil.dp2px(17);
    }

    @Override
    public TokenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.baseservice_item_token_icon, parent,false);
        return new TokenHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull TokenHolder holder, int position) {
        if(tokens.size()>5&&position==4){
            holder.tvNumber.setText("+"+tokens.size());
            holder.tvNumber.setVisibility(View.VISIBLE);

            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.ivToken.getLayoutParams();
            lp.width = width;
            lp.height = height;
            holder.ivToken.setLayoutParams(lp);
        }else if(tokens.size()<=5&&position==tokens.size()-1){
            holder.tvNumber.setVisibility(View.GONE);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.ivToken.getLayoutParams();
            lp.width = width;
            lp.height = height;
            holder.ivToken.setLayoutParams(lp);
        }else{
            holder.tvNumber.setVisibility(View.GONE);

            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.ivToken.getLayoutParams();
            lp.width = miniWidth;
            lp.height = height;
            holder.ivToken.setLayoutParams(lp);
        }

        TokenBean tokenBean = tokens.get(position);

        Glide.with(holder.ivToken.getContext())
                .load(tokenBean.getIconUrl())
                .apply(new RequestOptions()
                        .error(R.drawable.eos_ic_asset)
                        .placeholder(R.drawable.eos_ic_asset)
                        .dontAnimate()
                        .bitmapTransform(new BitmapTransformation() {
                            @Override
                            public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
                            }
                            @Override
                            protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
                                if (source == null) return null;
                                int inHeight = source.getHeight();
                                int inWidth = source.getWidth();
                                if (inHeight >= height)return source;
                                float fraction= height *1f/inHeight;
                                Bitmap result = pool.get((int) (inWidth*fraction), (int) (fraction*inHeight), Bitmap.Config.ARGB_8888);
                                if (result == null) {
                                    result = Bitmap.createBitmap((int) (inWidth*fraction), (int) (fraction*inHeight), Bitmap.Config.ARGB_8888);
                                }
                                Canvas canvas = new Canvas(result);
                                Matrix matrix = new Matrix();
                                matrix.setScale(fraction,fraction);
                                Paint paint = new Paint();
                                paint.setAntiAlias(true);
                                canvas.drawBitmap(source,matrix,paint);
                                return result;
                            }
                        }))
                .into(holder.ivToken);
    }


    @Override
    public int getItemCount() {
        if(tokens==null)return 0;
        return Math.min(5,tokens.size());
    }


    class TokenHolder extends RecyclerView.ViewHolder{

        public ImageView ivToken;
        public TextView tvNumber;
        public ViewGroup rootview;
        public TokenHolder(View itemView) {
            super(itemView);
            ivToken = itemView.findViewById(R.id.iv_token);
            tvNumber = itemView.findViewById(R.id.tv_token_number);
            rootview = itemView.findViewById(R.id.rootview);
        }
    }

}
