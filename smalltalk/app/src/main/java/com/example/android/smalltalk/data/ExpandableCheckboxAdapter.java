package com.example.android.smalltalk.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.android.smalltalk.R;
import com.example.android.smalltalk.SmalltalkUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Adapted from https://gist.github.com/Psest328/8762232 by shauna on 8/26/15.
 */

public class ExpandableCheckboxAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private HashMap<String, List<String>> mListDataChild; // Change ExpListChildItems to relevant data group
    private ArrayList<String> mListDataGroup; //Can change arraylist as needed
    private HashMap<Integer, boolean[]> mChildCheckStates; // Hashmap for keeping track of our checkbox check states
    private HashMap<Integer, List<String>> mChildIds; // Hashmap for keeping track of out child view IDs
    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;
    private String groupText;
    private String childText;
    private String childId;
    private Boolean mEditViewBoolean;
    private SmalltalkObject mObject; // Integer for keeping track of the main object ID.

    public ExpandableCheckboxAdapter(Context context, ArrayList<String> listDataGroup, HashMap<String,
            List<String>> listDataChild) {

        mContext = context;
        mEditViewBoolean = false;
        mListDataGroup = listDataGroup;
        mListDataChild = listDataChild;
        mChildCheckStates = new HashMap<Integer, boolean[]>();
    }

    public ExpandableCheckboxAdapter(Context context, RelatedObjectMap object_map, SmalltalkObject object) {

        mContext = context;
        mEditViewBoolean = true;
        mListDataGroup = object_map.header_names;
        mListDataChild = object_map.child_names;
        mChildCheckStates = object_map.child_checked_states;
        mChildIds = object_map.child_ids;
        mObject = object;

    }




    @Override
    public int getGroupCount() {
        return mListDataGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListDataChild.get(mListDataGroup.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mListDataGroup.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return mListDataChild.get(mListDataGroup.get(groupPosition)).get(childPosition);
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public final class GroupViewHolder {

        TextView mGroupText;
    }

    public final class ChildViewHolder {

        TextView mChildText;
        CheckBox mCheckBox;
        TextView mChildId;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        groupText = getGroup(groupPosition);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_headers, null);

            groupViewHolder = new GroupViewHolder(); // Initialize the GroupViewHolder defined at the bottom of this document

            groupViewHolder.mGroupText = (TextView) convertView.findViewById(R.id.expandable_list_header);

            convertView.setTag(groupViewHolder);
        } else {

            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.mGroupText.setText(groupText);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        childText = getChild(mGroupPosition, mChildPosition);
        groupText = getGroup(groupPosition);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_items, null);

            childViewHolder = new ChildViewHolder();

            childViewHolder.mChildText = (TextView) convertView
                    .findViewById(R.id.expandable_list_item);

            childViewHolder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.expandable_list_item_checkbox);

            childViewHolder.mChildId = (TextView) convertView .findViewById(R.id.expandable_list_item_edit_secret_id);

            convertView.setTag(R.layout.expandable_list_items, childViewHolder);

        } else {

            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.expandable_list_items);
        }

        childViewHolder.mChildText.setText(childText);

        if (mEditViewBoolean) {
            childId = mChildIds.get(mGroupPosition).get(mChildPosition);
            childViewHolder.mChildId.setText(childId);
        }

        if (mEditViewBoolean == true) {
            childViewHolder.mCheckBox.setVisibility(View.VISIBLE);
        }

        childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

        if (mChildCheckStates.containsKey(mGroupPosition)) {
            boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
            childViewHolder.mCheckBox.setChecked(getChecked[mChildPosition]);
        } else {
            boolean getChecked[] = new boolean[getChildrenCount(mGroupPosition)];
            mChildCheckStates.put(mGroupPosition, getChecked);
            childViewHolder.mCheckBox.setChecked(false);
        }

        childViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String item_type = mListDataGroup.get(mGroupPosition);
                String item_id = mChildIds.get(mGroupPosition).get(mChildPosition);

                if (isChecked) {

                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                    mObject.addRelationship(mContext, item_type, item_id);

                } else {

                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                    mObject.removeRelationship(mContext, item_type, item_id);

                }
            }
        });

        return convertView;
    }

}
