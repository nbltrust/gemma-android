package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.event.NodeSelectedEvent;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.adapter.VoteNodeListAdapter;
import com.cybex.gma.client.ui.model.vo.VoteNodeVO;
import com.cybex.gma.client.ui.presenter.VotePresenter;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomFullDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 投票主页面
 */
public class VoteFragment extends XFragment<VotePresenter> {

    @BindView(R.id.rv_list) RecyclerView mRecyclerView;
    @BindView(R.id.list_multiple_status_view) MultipleStatusView mStatusView;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_resource) TextView tvResource;
    @BindView(R.id.tv_vote_number) TextView tvVoteNumber;
    @BindView(R.id.tv_exec_vote) TextView tvExecVote;

    private final int EVENT_THIS_PAGE = 0;//本页面发送的事件
    private final int EVENT_DOWN = 1;//上级页面发送的事件
    private final int EVENT_UP = 2;//下级页面返回发送的事件
    private boolean hasDelegateRes = false;//是否有被抵押的资源
    private VoteNodeListAdapter adapter;
    private List<VoteNodeVO> nodeVOList = new ArrayList<>();
    private List<VoteNodeVO> selectedNodes = new ArrayList<>();//已选择的节点
    Unbinder unbinder;

    @OnClick(R.id.tv_exec_vote)
    public void vote(){
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

    public static VoteFragment newInstance() {
        Bundle args = new Bundle();
        VoteFragment fragment = new VoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.POSTING      , sticky = true)
    public void onNodeSelectChanged(NodeSelectedEvent event) {
        if (EmptyUtils.isNotEmpty(event)) {
            switch (event.getEventType()){
                case EVENT_THIS_PAGE:
                    //当前页面发送的事件
                    //动态更新底部已选投票数
                    tvVoteNumber.setText(String.format(getResources().getString(R.string.vote_num),
                            String.valueOf(selectedNodes.size())));
                    //动态设置底部textView颜色
                    if (selectedNodes.size() != 0){
                        //已选节点数不为0
                        tvVoteNumber.setBackground(getResources().getDrawable(R.drawable
                                .btn_vote_left_deep));
                        if (hasDelegateRes){
                            //如果有抵押的资源
                            tvExecVote.setClickable(true);
                            tvExecVote.setBackground(getResources().getDrawable(R.drawable.btn_vote_right_deep));
                        }else{
                            //没有被抵押的资源
                            tvExecVote.setClickable(false);
                            tvExecVote.setBackground(getResources().getDrawable(R.drawable.btn_vote_right_light));
                        }
                    }else {
                        //已选节点数为0
                        tvVoteNumber.setBackground(getResources().getDrawable(R.drawable.btn_vote_left_light));
                    }
                    break;
                case EVENT_DOWN:
                    //上级页面发送的事件
                    break;
                case EVENT_UP:
                    //下级页面发送的事件
                    selectedNodes.clear();
                    selectedNodes.addAll(event.getVoteNodeVOList());
                    tvVoteNumber.setText(String.format(getResources().getString(R.string.vote_num),
                            String.valueOf(selectedNodes.size())));
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(VoteFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        tvExecVote.setClickable(false);
        //getP().getTotalDelegatedRes();
        setNavibarTitle("投票", true, true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager
                .VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        getP().fetchBPDetail(ParamConstants.BP_NODE_NUMBERS);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                VoteNodeVO curVoteNodeVO = nodeVOList.get(position);

                if (curVoteNodeVO.ischecked) {
                    //从点选到取消
                    curVoteNodeVO.ischecked = false;
                    //从已选列表中删除
                    selectedNodes.remove(curVoteNodeVO);
                } else {
                    //点选
                    curVoteNodeVO.ischecked = true;
                    //向已选列表中添加
                    selectedNodes.add(curVoteNodeVO);
                }
                //此事件用于更新本页面UI
                NodeSelectedEvent event_this = new NodeSelectedEvent();
                event_this.setEventType(EVENT_THIS_PAGE);
                EventBusProvider.post(event_this);
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void initAdapterData(List<VoteNodeVO> voteNodeVOList) {
        nodeVOList.addAll(voteNodeVOList);
        adapter = new VoteNodeListAdapter(nodeVOList);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_vote;
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

    public void hasDelegatedRes(String cpu_amount, String net_amount){
        if (Double.parseDouble(cpu_amount) > 0 || Double.parseDouble(net_amount) > 0) {
            hasDelegateRes = true;
        }
        hasDelegateRes = false;
    }

    public void getTotalDelegatedResource(String total_amount){
        tvResource.setText(total_amount);
    }

    /**
     * 显示确认买入授权dialog
     */
    private void showConfirmAuthorDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_input_transfer_password, listenedItems, false, Gravity.BOTTOM);

        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_authorization:
                        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
                        if (EmptyUtils.isNotEmpty(curWallet)){
                            final String cypher = curWallet.getCypher();
                            EditText mPass = dialog.findViewById(R.id.et_password);
                            String inputPass = mPass.getText().toString().trim();
                            if (EmptyUtils.isNotEmpty(inputPass)){
                                final String key = JNIUtil.get_private_key(cypher, inputPass);
                                if (key.equals("wrong password")){
                                    GemmaToastUtils.showLongToast("密码错误！请重新输入！");
                                }else{
                                    //密码正确，执行投票操作
                                    final String curEOSName = curWallet.getCurrentEosName();
                                    List<String> producers = new ArrayList<>();
                                    for(VoteNodeVO vo : selectedNodes){
                                        producers.add(vo.getAccount());
                                    }
                                   // getP().executeVoteLogic(curEOSName, producers, key);
                                    dialog.cancel();
                                }
                            }else{
                                GemmaToastUtils.showLongToast("请输入密码！");
                            }

                        }
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }
}
