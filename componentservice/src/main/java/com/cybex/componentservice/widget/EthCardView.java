package com.cybex.componentservice.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cybex.componentservice.R;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.ui.adapter.TokenIconAdapter;
import com.cybex.componentservice.ui.adapter.decoration.LeftSpaceItemDecoration;
import com.cybex.componentservice.utils.SizeUtil;

import java.util.ArrayList;
import java.util.List;


public class EthCardView extends CardView{

    private View rootview;

    public EthCardView(Context context) {
        super(context);
        init(context,null);
    }

    public EthCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public EthCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    private RelativeLayout mLayoutInfo;
    private ImageView mIvEthIcon;
    private TextView mTotalEthAmount;
    private LinearLayout mViewEthTokens;
    private TextView mTvTokensNumber;
    private TextView mTvCurrencyType;
    private TextView mTotalPriceAmount;
    private RecyclerView mRvTokenIcons;

    private float ethNumber;
    private float totlePrice;
    private List<TokenBean> tokenList=new ArrayList<>();

    private TokenIconAdapter adapter;


    private void assignViews() {
        mLayoutInfo = (RelativeLayout) rootview.findViewById(R.id.layout_info);
        mIvEthIcon = (ImageView) rootview.findViewById(R.id.iv_eth_icon);
        mTotalEthAmount = (TextView) rootview.findViewById(R.id.total_eth_amount);
        mViewEthTokens = (LinearLayout) rootview.findViewById(R.id.view_eth_tokens);
        mTvTokensNumber = (TextView) rootview.findViewById(R.id.tv_tokens_number);
        mTvCurrencyType = (TextView) rootview.findViewById(R.id.tv_currency_type);
        mTotalPriceAmount = (TextView) rootview.findViewById(R.id.total_price_amount);
        mRvTokenIcons = (RecyclerView) rootview.findViewById(R.id.rv_token_icons);
    }


    public void init(Context context, AttributeSet attrs){
        rootview = LayoutInflater.from(context).inflate(R.layout.baseservice_item_eth_card, this);
//        addView(rootview);
        assignViews();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager
                .HORIZONTAL, false);
        mRvTokenIcons.setLayoutManager(layoutManager);

        tokenList.add(new TokenBean());
        tokenList.add(new TokenBean());
        tokenList.add(new TokenBean());

        adapter = new TokenIconAdapter(tokenList);

        mRvTokenIcons.setAdapter(adapter);

        mRvTokenIcons.addItemDecoration(new LeftSpaceItemDecoration(SizeUtil.dp2px(-12)));

        updateTokenView();

    }


    public void updateTokenView(){
        mTvTokensNumber.setText(tokenList.size()+"");
        if(tokenList.size()>0){
            mViewEthTokens.setVisibility(View.VISIBLE);
        }else{
            mViewEthTokens.setVisibility(View.GONE);
        }
    }


    public float getEthNumber() {
        return ethNumber;
    }

    public void setEthNumber(float ethNumber) {
        this.ethNumber = ethNumber;
        mTotalEthAmount.setText(Float.toString(ethNumber));
    }

    public float getTotlePrice() {
        return totlePrice;
    }

    public void setTotlePrice(float totlePrice) {
        this.totlePrice = totlePrice;
        mTotalPriceAmount.setText(Float.toString(totlePrice));
    }

    public List<TokenBean> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<TokenBean> tokenList) {
        this.tokenList.clear();
        this.tokenList.addAll(tokenList);
        adapter.notifyDataSetChanged();
        updateTokenView();
    }

    public void setTokenListVisibility(boolean isVisibility){

        if(isVisibility){
            mRvTokenIcons.setVisibility(View.VISIBLE);
        }else{
            mRvTokenIcons.setVisibility(View.GONE);
        }
    }


}
