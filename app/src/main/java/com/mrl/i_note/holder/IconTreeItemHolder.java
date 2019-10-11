package com.mrl.i_note.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.mrl.i_note.R;
import com.mrl.i_note.activity.MainActivity;
import com.mrl.i_note.fragment.FolderStructureFragment;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private PrintView arrowView;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        final PrintView iconView = (PrintView) view.findViewById(R.id.icon);
        iconView.setIconText(context.getResources().getString(value.icon));

        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);

        view.findViewById(R.id.btn_viewDetial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderStructureFragment.viewDetialListener(node);
            }
        });

        if(MainActivity.getMainActivity().getBefforAction()==MainActivity.AFTER_USERLIST){
            view.findViewById(R.id.btn_addFolder).setVisibility(View.GONE);
            view.findViewById(R.id.btn_delete).setVisibility(View.GONE);
            view.findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FolderStructureFragment.viewCommentListener(node);
                }
            });
        }else{
            view.findViewById(R.id.btn_comment).setVisibility(View.GONE);
            view.findViewById(R.id.btn_addFolder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FolderStructureFragment.addFolderListener(node);
                }
            });
            view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                getTreeView().removeNode(node);
                    FolderStructureFragment.deleteFolderListener(node);
                }
            });
        }

        //if My computer
        if (node.getLevel() == 1) {
            view.findViewById(R.id.btn_delete).setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class IconTreeItem<E> {
        public int icon;
        public String text;
        public E obj;

        public IconTreeItem(int icon, String text, E obj) {
            this.icon = icon;
            this.text = text;
            this.obj = obj;
        }
    }
}
