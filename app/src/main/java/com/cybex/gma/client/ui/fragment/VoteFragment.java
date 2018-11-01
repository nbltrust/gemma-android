package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.cybex.base.view.refresh.CommonRefreshLayout;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.PasswordValidateHelper;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.NodeSelectedEvent;
import com.cybex.gma.client.ui.adapter.VoteNodeListAdapter;
import com.cybex.gma.client.ui.model.vo.VoteNodeVO;
import com.cybex.gma.client.ui.presenter.VotePresenter;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.siberiadante.customdialoglib.CustomDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import seed39.Seed39;

/**
 * 投票主页面
 */
public class VoteFragment extends XFragment<VotePresenter> {

    @BindView(R.id.rv_list) RecyclerView mRecyclerView;
    @BindView(R.id.list_multiple_status_view) MultipleStatusView listMultipleStatusView;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_resource) TextView tvResource;
    @BindView(R.id.tv_vote_number) TextView tvVoteNumber;
    @BindView(R.id.tv_exec_vote) TextView tvExecVote;
    @BindView(R.id.view_refresh) CommonRefreshLayout viewRefresh;

    private int inputCount;

    private final int EVENT_THIS_PAGE = 0;//本页面发送的事件
    private final int EVENT_DOWN = 1;//上级页面发送的事件
    private final int EVENT_UP = 2;//下级页面返回发送的事件
    private final int SELECTED_NODES_LIMIT = 30;
    private boolean hasDelegateRes = false;//是否有被抵押的资源
    private VoteNodeListAdapter mAdapter;
    private List<VoteNodeVO> nodeVOList = new ArrayList<>();
    private List<VoteNodeVO> selectedNodes = new ArrayList<>();//已选择的节点
    Unbinder unbinder;

    @OnClick(R.id.tv_exec_vote)
    public void vote() {
        showConfirmAuthorDialog();
    }

    @OnClick(R.id.tv_vote_number)
    public void goToSeeSelectedNodes() {
        //此事件用于向下级页面传递数据
        NodeSelectedEvent event = new NodeSelectedEvent();
        event.setEventType(EVENT_DOWN);
        event.setVoteNodeVOList(selectedNodes);
        EventBusProvider.postSticky(event);
        start(NodeSelectedFragment.newInstance());
    }

    public static VoteFragment newInstance(Bundle args) {
        VoteFragment fragment = new VoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onNodeSelectChanged(NodeSelectedEvent event) {
        if (EmptyUtils.isNotEmpty(event)) {
            switch (event.getEventType()) {
                case EVENT_THIS_PAGE:
                    //当前页面发送的事件
                    //动态更新底部已选投票数
                    tvVoteNumber.setText(String.format(getResources().getString(R.string.eos_amount_vote_num),
                            String.valueOf(selectedNodes.size())));
                    //动态设置底部textView颜色
                    if (selectedNodes.size() != 0) {
                        //已选节点数不为0
                        tvVoteNumber.setBackground(getResources().getDrawable(R.drawable
                                .eos_btn_vote_left_deep));
                        LoggerManager.d("hasDelegateRes", hasDelegateRes);
                        if (hasDelegateRes) {
                            //如果有抵押的资源
                            tvExecVote.setClickable(true);
                            tvExecVote.setText(getResources().getString(R.string.eos_title_vote));
                            tvExecVote.setBackground(getResources().getDrawable(R.drawable.eos_btn_vote_right_deep));
                        } else {
                            //没有被抵押的资源
                            tvExecVote.setClickable(false);
                            tvExecVote.setText(getResources().getString(R.string.eos_tip_no_avail_votes));
                            tvExecVote.setBackground(getResources().getDrawable(R.drawable.eos_btn_vote_right_light));
                        }
                    } else {
                        //已选节点数为0
                        tvVoteNumber.setBackground(getResources().getDrawable(R.drawable.eos_btn_vote_left_light));
                        tvExecVote.setClickable(false);
                        tvExecVote.setText(getResources().getString(R.string.eos_title_vote));
                        tvExecVote.setBackground(getResources().getDrawable(R.drawable.eos_btn_vote_right_light));
                    }
                    break;
                case EVENT_DOWN:
                    //上级页面发送的事件
                    break;
                case EVENT_UP:
                    //下级页面发送的事件
                    selectedNodes.clear();
                    selectedNodes.addAll(event.getVoteNodeVOList());
                    tvVoteNumber.setText(String.format(getResources().getString(R.string.eos_amount_vote_num),
                            String.valueOf(selectedNodes.size())));
                    if (selectedNodes.size() == 0) {
                        //从下级页面回退时把所有节点取消了
                        tvExecVote.setClickable(false);
                        tvExecVote.setBackground(getResources().getDrawable(R.drawable.eos_btn_vote_right_light));
                        tvVoteNumber.setBackground(getResources().getDrawable(R.drawable.eos_btn_vote_left_light));
                    }
                    break;
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(VoteFragment.this, rootView);
        setNavibarTitle(getResources().getString(R.string.eos_tip_vote), true, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        //加载节点信息
        getP().fetchBPDetail(ParamConstants.BP_NODE_NUMBERS);
        tvExecVote.setClickable(false);
        inputCount = 0;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager
                .VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new SimpleClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VoteNodeVO curVoteNodeVO = nodeVOList.get(position);
                LoggerManager.d("position", position);
                if (curVoteNodeVO.ischecked) {
                    //从点选到取消
                    curVoteNodeVO.ischecked = false;
                    //从已选列表中删除
                    selectedNodes.remove(curVoteNodeVO);
                } else {
                    //点选
                    if (selectedNodes.size() < SELECTED_NODES_LIMIT) {
                        //向已选列表中添加
                        curVoteNodeVO.ischecked = true;
                        selectedNodes.add(curVoteNodeVO);
                    } else {
                        GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_tip_selected_nodes_limit));
                    }
                }
                //此事件用于更新本页面UI
                NodeSelectedEvent event_this = new NodeSelectedEvent();
                event_this.setEventType(EVENT_THIS_PAGE);
                EventBusProvider.post(event_this);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                VoteNodeVO curVoteNodeVO = nodeVOList.get(position);

                if (curVoteNodeVO.ischecked) {
                    //从点选到取消
                    curVoteNodeVO.ischecked = false;
                    //从已选列表中删除
                    selectedNodes.remove(curVoteNodeVO);
                } else {
                    //点选
                    if (selectedNodes.size() < SELECTED_NODES_LIMIT) {
                        //向已选列表中添加
                        curVoteNodeVO.ischecked = true;
                        selectedNodes.add(curVoteNodeVO);
                    } else {
                        GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_tip_selected_nodes_limit));
                    }
                }
                //此事件用于更新本页面UI
                NodeSelectedEvent event_this = new NodeSelectedEvent();
                event_this.setEventType(EVENT_THIS_PAGE);
                EventBusProvider.post(event_this);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        //下拉刷新，上拉加载监听设置
        viewRefresh.setEnableLoadmore(false);
        viewRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getP().fetchBPDetail(ParamConstants.BP_NODE_NUMBERS);
                selectedNodes.clear();
                NodeSelectedEvent event = new NodeSelectedEvent();
                event.setEventType(EVENT_THIS_PAGE);
                EventBusProvider.post(event);
                viewRefresh.finishRefresh();
                viewRefresh.setLoadmoreFinished(true);
            }
        });

    }

    public void initAdapterData(List<VoteNodeVO> voteNodeVOList) {
        nodeVOList.clear();
        nodeVOList.addAll(voteNodeVOList);
        mAdapter = new VoteNodeListAdapter(nodeVOList);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_vote;
    }

    @Override
    public VotePresenter newP() {
        return new VotePresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        selectedNodes.clear();
    }

    public void showLoading() {
        listMultipleStatusView.showLoading();
    }

    public void showError() {
        listMultipleStatusView.showError();
    }

    public void showContent() {
        listMultipleStatusView.showContent();
    }

    /**
     * 显示加载更多完成和空数据界面逻辑
     */
    public void showEmptyOrFinish() {
        if (mAdapter != null) {
            List<VoteNodeVO> nodeVOList = mAdapter.getData();
            if (EmptyUtils.isEmpty(nodeVOList)) {
                listMultipleStatusView.showEmpty();
            } else {
                listMultipleStatusView.showContent();
            }

        } else {
            listMultipleStatusView.showEmpty();
        }
    }

    public void hasDelegatedRes(boolean status) {
        hasDelegateRes = status;
        if (hasDelegateRes) {
            tvExecVote.setText(getString(R.string.eos_title_vote));
        } else {
            tvExecVote.setText(getString(R.string.eos_tip_no_avail_votes));
        }
    }

    public void setTotalDelegatedResource(String total_amount) {
        tvResource.setText(total_amount);
        /*
        if (EmptyUtils.isNotEmpty(selectedNodes)){
            tvExecVote.setClickable(true);
            tvExecVote.setBackground(getResources().getDrawable(R.drawable.eos_btn_vote_right_deep));
        }else {
            tvExecVote.setClickable(false);
            tvExecVote.setBackground(getResources().getDrawable(R.drawable.eos_btn_vote_right_light));
        }
        */
    }

    /**
     * 显示投票授权dialog
     */
    private void showConfirmAuthorDialog() {
        MultiWalletEntity wallet = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();
        PasswordValidateHelper passwordValidateHelper = new PasswordValidateHelper(wallet, context);
        passwordValidateHelper.startValidatePassword(
                new PasswordValidateHelper.PasswordValidateCallback() {
                    @Override
                    public void onValidateSuccess(String password) {
                        //密码正确，执行投票操作
                        final String curEOSName = wallet.getEosWalletEntities().get(0).getCurrentEosName();
                        String saved_pri_key = wallet.getEosWalletEntities().get(0).getPrivateKey();
                        final String key = Seed39.keyDecrypt(password, saved_pri_key);
                        List<String> producers = new ArrayList<>();
                        for (VoteNodeVO vo : selectedNodes) {
                            producers.add(vo.getAccount());
                        }
                        Collections.sort(producers);
                        getP().executeVoteLogic(curEOSName, producers, key);
                    }

                    @Override
                    public void onValidateFail(int failedCount) {
                            showPasswordHintDialog();
                    }
                });


//        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
//        CustomFullDialog dialog = new CustomFullDialog(getContext(),
//                R.layout.eos_dialog_input_password_with_ic_mask, listenedItems, false, Gravity.BOTTOM);
//
//        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
//            @Override
//            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
//                switch (view.getId()) {
//                    case R.id.imc_cancel:
//                        dialog.cancel();
//                        break;
//                    case R.id.btn_confirm_authorization:
//                        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
//                        EosWalletEntity curEosWallet = curWallet.getEosWalletEntities().get(0);
//                        if (EmptyUtils.isNotEmpty(curWallet) && EmptyUtils.isNotEmpty(curEosWallet)){
//                            final String cypher = curWallet.getCypher();
//                            EditText mPass = dialog.findViewById(R.id.et_password);
//                            String inputPass = mPass.getText().toString().trim();
//                            if (EmptyUtils.isNotEmpty(inputPass)){
//                                final String key = JNIUtil.get_private_key(cypher, inputPass);
//
//                                if (key.equals("wrong password")){
//                                    GemmaToastUtils.showLongToast(getString(R.string.eos_tip_wrong_password));
//
//                                    inputCount++;
//                                    if (inputCount > 3){
//                                        dialog.cancel();
//                                        showPasswordHintDialog();
//                                    }
//
//                                }else{
//                                    //密码正确，执行投票操作
//                                    final String curEOSName = curEosWallet.getCurrentEosName();
//                                    List<String> producers = new ArrayList<>();
//                                    for(VoteNodeVO vo : selectedNodes){
//                                        producers.add(vo.getAccount());
//                                    }
//                                    Collections.sort(producers);
//                                    getP().executeVoteLogic(curEOSName, producers, key);
//                                    dialog.cancel();
//                                }
//                            }else{
//                                GemmaToastUtils.showLongToast(getString(R.string.eos_tip_please_input_pass));
//                            }
//
//                        }
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        dialog.show();
//        if (getArguments() != null){
//            String eosname = getArguments().getString("cur_eos_name");
//            EditText etPasword = dialog.findViewById(R.id.et_password);
//            etPasword.setHint("请输入@" + eosname + "的密码");
//        }
    }

    /**
     * 显示密码提示Dialog
     */
    private void showPasswordHintDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.eos_dialog_password_hint, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        dialog.cancel();
                        showConfirmAuthorDialog();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        TextView tv_pass_hint = dialog.findViewById(R.id.tv_password_hint_hint);
        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)) {
            String passHint = curWallet.getPasswordTip();
            String showInfo = getString(R.string.eos_tip_password_hint) + " : " + passHint;
            tv_pass_hint.setText(showInfo);
        }
    }
}
