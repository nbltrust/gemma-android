package com.cybex.gma.client.widget;

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

import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.ui.adapter.decoration.LeftSpaceItemDecoration;
import com.cybex.componentservice.utils.AmountUtil;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.gma.client.R;
import com.cybex.componentservice.ui.adapter.TokenIconAdapter;

import java.util.ArrayList;
import java.util.List;


public class EosCardView extends CardView{

    private View rootview;

    public EosCardView(Context context) {
        super(context);
        init(context,null);
    }

    public EosCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public EosCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

//    public EosCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context,attrs);
//    }


    private RelativeLayout mLayoutInfo;
    private ImageView mIvEosIcon;
    private TextView mTvAccountStatus;
    private TextView mTotalEosAmount;
    private LinearLayout mViewEosTokens;
    private TextView mToken;
    private TextView mTvTokensNumber;


    public void setCurrencyType(int currencyType) {
        if (currencyType == CacheConstants.CURRENCY_CNY){
            mTvCurrencyType.setText("≈ ¥ ");
        }else if (currencyType == CacheConstants.CURRENCY_USD){
            mTvCurrencyType.setText("≈ $ ");
        }else {
            mTvCurrencyType.setText("≈ ¥ ");
        }
    }

    private TextView mTvCurrencyType;
    private TextView mTotalPriceAmount;
    private RecyclerView mRvTokenIcons;

//    private int tokenTypeNumber;
    private float eosNumber;
    private String totlePrice;
    private List<TokenBean> tokenList=new ArrayList<>();
    private int state;
    private String accountName;
    private int createProgress;

    private TokenIconAdapter adapter;

    public static final int STATE_WAIT_NOTIFY=0;
    public static final int STATE_CREATING=1;
    public static final int STATE_CREATED=2;


    private void assignViews() {
        mLayoutInfo = (RelativeLayout) rootview.findViewById(R.id.layout_info);
        mIvEosIcon = (ImageView) rootview.findViewById(R.id.iv_eos_icon);
        mTvAccountStatus = (TextView) rootview.findViewById(R.id.tv_account_status);
        mTotalEosAmount = (TextView) rootview.findViewById(R.id.total_eos_amount);
        mViewEosTokens = (LinearLayout) rootview.findViewById(R.id.view_eos_tokens);
        mTvTokensNumber = (TextView) rootview.findViewById(R.id.tv_tokens_number);
        mTvCurrencyType = (TextView) rootview.findViewById(R.id.tv_currency_type);
        mTotalPriceAmount = (TextView) rootview.findViewById(R.id.total_price_amount);
        mRvTokenIcons = (RecyclerView) rootview.findViewById(R.id.rv_token_icons);
        mToken = rootview.findViewById(R.id.tv_token);
    }


    public void init(Context context, AttributeSet attrs){
        rootview = LayoutInflater.from(context).inflate(R.layout.item_eos_card, this);
//        addView(rootview);
        assignViews();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager
                .HORIZONTAL, false);
        mRvTokenIcons.setLayoutManager(layoutManager);


//        tokenList.add(new TokenBean());
        adapter = new TokenIconAdapter(tokenList);

        mRvTokenIcons.setAdapter(adapter);

        mRvTokenIcons.addItemDecoration(new LeftSpaceItemDecoration(SizeUtil.dp2px(-12)));


        updateTokenView();
    }

    public void updateTokenView(){
        mTvTokensNumber.setText(tokenList.size()+"");
        if(tokenList.size()>0){
            mViewEosTokens.setVisibility(View.VISIBLE);
            if (tokenList.size() == 1){
                mToken.setText("token");
            }else {
                mToken.setText("tokens");
            }

        }else{
            mViewEosTokens.setVisibility(View.GONE);
        }
    }



    public float getEosNumber() {
        return eosNumber;
    }

    public void setEosNumber(float eosNumber) {
        this.eosNumber = eosNumber;
        mTotalEosAmount.setText(AmountUtil.round(Float.toString(eosNumber), 4));
    }

    public String getTotlePrice() {
        return totlePrice;
    }

    public void setTotlePrice(String totlePrice) {
        this.totlePrice = totlePrice;
        mTotalPriceAmount.setText(totlePrice);
    }

    public List<TokenBean> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<TokenBean> tokenList) {
        this.tokenList.clear();
//        this.tokenList.add(new TokenBean("","http://img5.imgtn.bdimg.com/it/u=2321164342,3736510307&fm=26&gp=0.jpg"));
//        this.tokenList.add(new TokenBean("","https://eosauthority.com/common/images/airdrops_EETH.png"));
        this.tokenList.addAll(tokenList);
        adapter.notifyDataSetChanged();
        updateTokenView();
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        updateAccountState();
    }

    /**
     * 根据state显示不同情况下的账户状态
     */
    private void updateAccountState() {
        if(state==STATE_WAIT_NOTIFY){
            mTvAccountStatus.setText(getResources().getText(R.string.card_eos_wait_notify));
        }else if(state==STATE_CREATING){
            mTvAccountStatus.setText(String.format(this.getResources().getString(R.string
                    .card_eos_creating_progress),createProgress));
            //mTvAccountStatus.setText(getResources().getText(R.string.account_confirming));
        }else if(state==STATE_CREATED){
            mTvAccountStatus.setText(accountName);
        }
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
        updateAccountState();
    }

    public int getCreateProgress() {
        return createProgress;
    }

    public void setCreateProgress(int createProgress) {
        this.createProgress = createProgress;
        updateAccountState();
    }

}
