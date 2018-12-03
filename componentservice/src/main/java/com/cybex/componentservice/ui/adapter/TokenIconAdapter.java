package com.cybex.componentservice.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cybex.componentservice.R;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.utils.SizeUtil;
import java.util.List;

public class TokenIconAdapter extends RecyclerView.Adapter<TokenIconAdapter.TokenHolder> {




    private final List<TokenBean> tokens;
    private float outerHeight;
    private float height;
    private float width;
    private float miniWidth;

    public TokenIconAdapter(
            List<TokenBean> token) {
        this.tokens=token;

        outerHeight= SizeUtil.getDisplayMetrics().scaledDensity*28;
        height= SizeUtil.getDisplayMetrics().scaledDensity*27;
        width= SizeUtil.getDisplayMetrics().scaledDensity*27;
        miniWidth= SizeUtil.getDisplayMetrics().scaledDensity*17;
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

        }else if(tokens.size()<=5&&position==tokens.size()-1){
            holder.tvNumber.setVisibility(View.GONE);
        }else{
            holder.tvNumber.setVisibility(View.GONE);
        }

        TokenBean tokenBean = tokens.get(position);
        String tokenUrl = tokenBean.getLogo_url();

//        LoggerManager.e("outerHeight:"+outerHeight);

        Bitmap  outerLine = Bitmap.createBitmap((int) outerHeight, (int) outerHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(outerLine);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#dddddd"));
        paint.setStrokeWidth(2);
        canvas.drawCircle(outerHeight / 2, outerHeight / 2, outerHeight / 2 - 1, paint);
        holder.ivOuter.setImageBitmap(outerLine);

//        LoggerManager.e("tokenUrl="+tokenUrl);
        if (tokenUrl == null || tokenUrl.equals("")){
            Glide.with(holder.ivToken.getContext())
                    .load(R.drawable.ic_token_unknown)
                    .into(holder.ivToken);
        }else {
            Glide.with(holder.ivToken.getContext())
                    .load(tokenBean.getLogo_url())
                    .apply(new RequestOptions()
                            .error(R.drawable.ic_token_unknown)
                            .placeholder(R.drawable.ic_token_unknown)
                            .dontAnimate()
                    )
                    .into(holder.ivToken);
        }
    }


    @Override
    public int getItemCount() {
        if(tokens==null)return 0;
        return Math.min(5,tokens.size());
    }


    class TokenHolder extends RecyclerView.ViewHolder{

        public ImageView ivToken;
        public ImageView ivOuter;
        public TextView tvNumber;
        public ViewGroup rootview;
        public TokenHolder(View itemView) {
            super(itemView);
            ivToken = itemView.findViewById(R.id.iv_token);
            ivOuter = itemView.findViewById(R.id.iv_outer_circle);
            tvNumber = itemView.findViewById(R.id.tv_token_number);
            rootview = itemView.findViewById(R.id.rootview);
        }
    }

}
