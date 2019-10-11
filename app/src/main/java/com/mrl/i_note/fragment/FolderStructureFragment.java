package com.mrl.i_note.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mrl.i_note.R;
import com.mrl.i_note.activity.CommentActivity;
import com.mrl.i_note.activity.EditActivity;
import com.mrl.i_note.activity.MainActivity;
import com.mrl.i_note.activity.UserListActivity;
import com.mrl.i_note.customView.CustomAlertDiglog;
import com.mrl.i_note.holder.IconTreeItemHolder;
import com.mrl.i_note.model.Knowledge;
import com.mrl.i_note.service.KnowledgeService;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class FolderStructureFragment extends Fragment {
    private static TextView statusBar;
    private TreeNode root;
    private ViewGroup containerView;

    private AndroidTreeView tView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = TreeNode.root();
        View rootView = inflater.inflate(R.layout.fragment_default, null, false);
        containerView = (ViewGroup) rootView.findViewById(R.id.container);
        statusBar = (TextView) rootView.findViewById(R.id.status_bar);
        initTree();
        if (getArguments() != null) {
            String state = getArguments().getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }
        containerView.addView(tView.getView());
        return rootView;
        //return inflater.inflate(R.layout.fragment_collect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initTree(){
        List<Knowledge> knowledges = MainActivity.getMainActivity().getFriendKnowledges();
        //先检查MainActivity保存的社区其他成员的note是否为空，为空就加载本地数据作为TreeView的节点数据，否则就以friendKnowledges为TreeView的节点数据
        if(knowledges==null){
            KnowledgeService knowledgeService = MainActivity.getMainActivity().getKnowledgeService();
            knowledges = knowledgeService.getKnowledges("view_time>=?", new String[]{"0"}, null);
            //如果本地数据为空，就重新生成基本数据，这种情况一般会在app安装后第一次使用时发生
            if(knowledges==null||knowledges.size()<=0){
                knowledgeService.initBaseData(null);
                knowledges = knowledgeService.getKnowledges("view_time>=?", new String[]{"0"}, null);
            }
        }
        //以下为TreeView树结构生成算法
        Map<String,Knowledge> knowledgeMap = null;
        Map<String,TreeNode> nodeMap = null;
        if(knowledges!=null&&knowledges.size()>0){
            knowledgeMap = new HashMap<String, Knowledge>();
            nodeMap = new HashMap<String, TreeNode>();
            for(Knowledge knowledge:knowledges){
                knowledgeMap.put(knowledge.getId(), knowledge);
                TreeNode node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, knowledge.getTitle(), knowledge));
                nodeMap.put(knowledge.getId(),node);
            }
            for(Knowledge knowledge:knowledges){
                if(knowledge.getParentId()==null||"".equals(knowledge.getParentId()==null)){
                    root.addChild(nodeMap.get(knowledge.getId()));
                }else{
                    TreeNode parent = nodeMap.get(knowledge.getParentId());
                    if (parent!=null){
                        parent.addChild(nodeMap.get(knowledge.getId()));
                    }
                }
            }
        }
        tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        //tView.setDefaultNodeClickListener(nodeClickListener);
        if(!(MainActivity.getMainActivity().getBefforAction()==MainActivity.AFTER_USERLIST)){
            tView.setDefaultNodeLongClickListener(nodeLongClickListener);
        }
    }

//    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
//        @Override
//        public void onClick(TreeNode node, Object value) {
//
//        }
//    };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            Knowledge currentKnowledge = (Knowledge) item.obj;
            if(currentKnowledge != null){
                toEdit(currentKnowledge);
            }else{
                Toast.makeText(getActivity(), "获取失败: " + item.text, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    private static void toEdit(Knowledge knowledge){
        Intent intent = new Intent(MainActivity.getMainActivity(), EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("knowledge", knowledge);
        intent.putExtras(bundle);
        MainActivity.getMainActivity().startActivity(intent);
        MainActivity.getMainActivity().overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }

    public static void viewDetialListener(TreeNode node) {
        IconTreeItemHolder.IconTreeItem iconTreeItem = (IconTreeItemHolder.IconTreeItem)(node.getValue());
        statusBar.setText("Last clicked: " + iconTreeItem.text);
        Knowledge currentKnowledge = (Knowledge) iconTreeItem.obj;
        if(currentKnowledge!=null){
            boolean isWebImg = false;
            if(MainActivity.getMainActivity().getBefforAction()==MainActivity.AFTER_USERLIST){
                isWebImg = true;
            }
            CustomAlertDiglog.initDetialDialog(MainActivity.getMainActivity(), currentKnowledge, isWebImg);
            currentKnowledge.setViewTime(currentKnowledge.getViewTime()+1);
            if(MainActivity.getMainActivity().getBefforAction()!=MainActivity.AFTER_USERLIST){
                KnowledgeService knowledgeService = MainActivity.getMainActivity().getKnowledgeService();
                knowledgeService.updateKnowledge(currentKnowledge);
            }
        }else{
            Toast.makeText(MainActivity.getMainActivity(), "获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    public static void viewCommentListener(TreeNode node) {
        IconTreeItemHolder.IconTreeItem iconTreeItem = (IconTreeItemHolder.IconTreeItem)(node.getValue());
        Knowledge currentKnowledge = (Knowledge) iconTreeItem.obj;
        if(currentKnowledge!=null){
            Intent intent = new Intent(MainActivity.getMainActivity(), CommentActivity.class);
            intent.putExtra("knowledgeId", currentKnowledge.getId());
            MainActivity.getMainActivity().startActivity(intent);
            MainActivity.getMainActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }else{
            Toast.makeText(MainActivity.getMainActivity(), "获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    public static void addFolderListener(TreeNode node) {
        IconTreeItemHolder.IconTreeItem iconTreeItem = (IconTreeItemHolder.IconTreeItem)(node.getValue());
        Knowledge currentKnowledge = (Knowledge) iconTreeItem.obj;
        if(currentKnowledge!=null){
            Knowledge knowledge = new Knowledge();
            knowledge.setParentId(currentKnowledge.getId());
            toEdit(knowledge);
        }else{
            Toast.makeText(MainActivity.getMainActivity(), "获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteFolderListener(TreeNode node) {
        IconTreeItemHolder.IconTreeItem iconTreeItem = (IconTreeItemHolder.IconTreeItem)(node.getValue());
        Knowledge currentKnowledge = (Knowledge) iconTreeItem.obj;
        List<Knowledge> knowledges = new ArrayList<>();
        knowledges.add(currentKnowledge);
        CustomAlertDiglog.initExecDialog(MainActivity.getMainActivity(), knowledges,
                MainActivity.getMainActivity().getKnowledgeService(), MainActivity.getMainActivity().getDayNightTheme(), CustomAlertDiglog.DELETE);
    }
}
