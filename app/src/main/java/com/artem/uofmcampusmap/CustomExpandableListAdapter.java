package com.artem.uofmcampusmap;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeaders;
    private HashMap<String, List<String>> listDataChilds;

    public CustomExpandableListAdapter(Context context, List<String> listDataHeaders,
                                       HashMap<String, List<String>> listDataChilds)
    {
        this.context = context;
        this.listDataChilds = listDataChilds;
        this.listDataHeaders = listDataHeaders;
    }

    @Override
    public int getGroupCount() {
        return listDataHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childCount = 0;

        if(groupPosition != 0 && groupPosition != 1)
        {
            childCount = listDataChilds.get(listDataHeaders.get(groupPosition)).size();
        }

        return childCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeaders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDataChilds.get(listDataHeaders.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        String groupTitle = (String) getGroup(groupPosition);

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView groupTitleTV = convertView.findViewById(R.id.tv_group_title);
        groupTitleTV.setText(groupTitle);

        ImageView groupArrowImage = convertView.findViewById(R.id.iv_arrow_dropdown);

        if(groupPosition != 0 && groupPosition != 1)
        {
            if (isExpanded)
            {
                groupArrowImage.setImageResource(R.drawable.ic_group_collapse_00);
            } else {
                groupArrowImage.setImageResource(R.drawable.ic_group_expand_00);
            }
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        String childTitle = (String) getChild(groupPosition, childPosition);

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView childTitleTV = convertView.findViewById(R.id.tv_item_title);
        childTitleTV.setText(childTitle);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
