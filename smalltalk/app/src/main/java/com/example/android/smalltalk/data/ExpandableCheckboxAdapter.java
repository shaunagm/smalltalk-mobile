package com.example.android.smalltalk.data;

import android.content.Context;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.smalltalk.R;
import com.example.android.smalltalk.SmalltalkUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Adapted from https://gist.github.com/Psest328/8762232 by shauna on 8/26/15.
 */

public class ExpandableCheckboxAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private SmalltalkObject mObject;
    private Boolean mEditViewBoolean;

    private ArrayList<String> mGroupNames; // Group names (rename!)
    private HashMap<String, List<String>> mChildNames; // Hashmap of child names
    private HashMap<String, List<String>> mChildIDs; // Hashmap of child IDs
    private HashMap<String, boolean[]> mCheckStates; // Hashmap for checkbox check states

    public ExpandableCheckboxAdapter(Context context, SmalltalkObject current_object, Boolean is_edit_view) {

        mContext = context;
        mObject = current_object;
        mEditViewBoolean = is_edit_view;

        mGroupNames = new ArrayList<String>(Arrays.asList(mObject.getRelatedHeaders()));

        // Initialize!
        mChildNames = new HashMap<String, List<String>>();
        mChildIDs = new HashMap<String, List<String>>();
        mCheckStates = new HashMap<String, boolean[]>();


        if (!mEditViewBoolean) {

            for (String group : mGroupNames) {
                Pair names_and_ids = mObject.getRelatedNamesAndIDs(group);
                ArrayList<String> child_names = (ArrayList<String>) names_and_ids.first;
                ArrayList<String> child_ids = (ArrayList<String>) names_and_ids.second;
                mChildNames.put(group, child_names);
                mChildIDs.put(group, child_ids);
            }

        } else {

            for (String group : mGroupNames) {
                Pair names_and_ids = mObject.getAllItemsNamesAndIDs(group);
                ArrayList<String> child_names = (ArrayList<String>) names_and_ids.first;
                ArrayList<String> child_ids = (ArrayList<String>) names_and_ids.second;
                mChildNames.put(group, child_names);
                mChildIDs.put(group, child_ids);

                // Compare related names with all names to get a boolean of checked states
                List<String> related_names = mObject.getRelatedNames(group);
                boolean[] is_checked_array = new boolean[child_names.size()];
                for (int i = 0; i < child_names.size(); i++) {
                    if (related_names.contains(child_names.get(i))) {
                        is_checked_array[i] = true;
                    } else {
                        is_checked_array[i] = false;
                    }
                }
                mCheckStates.put(group, is_checked_array);
            }
        }
    }

    @Override
    public int getGroupCount() {
        return mGroupNames.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildNames.get(mGroupNames.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mGroupNames.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return mChildNames.get(mGroupNames.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public String getChildDBId(int groupPosition, int childPosition) {
        return mChildIDs.get(mGroupNames.get(groupPosition)).get(childPosition);
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
        ImageButton mStar;
        ImageButton mArchive;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        
        GroupViewHolder groupViewHolder;

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

        groupViewHolder.mGroupText.setText(getGroup(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        // Set some vars
        final String childText = getChild(groupPosition, childPosition);
        final String groupText = getGroup(groupPosition);
        final String item_id = mChildIDs.get(groupText).get(childPosition);
        final int group_position = groupPosition;
        final int child_position = childPosition;

        ChildViewHolder childViewHolder;

        // Inflate the view
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_items, null);

            childViewHolder = new ChildViewHolder();
            childViewHolder.mChildText = (TextView) convertView.findViewById(R.id.expandable_list_item);
            childViewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.expandable_list_item_checkbox);
            childViewHolder.mChildId = (TextView) convertView .findViewById(R.id.expandable_list_item_edit_secret_id);
            childViewHolder.mStar = (ImageButton) convertView.findViewById(R.id.small_star_button);
            childViewHolder.mArchive = (ImageButton) convertView.findViewById(R.id.small_archive_button);

            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
            // If re-using a view, make sure the star & archive options are returned to invisibility.
            childViewHolder.mArchive.setVisibility(View.GONE);
            childViewHolder.mStar.setVisibility(View.GONE);
        }

        // Set universal view elements
        childViewHolder.mChildText.setText(childText);
        childViewHolder.mChildId.setText(item_id);

        // If edit view, handle checkbox logic
        if (mEditViewBoolean) {

            childViewHolder.mCheckBox.setVisibility(View.VISIBLE);

            if (mCheckStates.containsKey(groupText)) {
                boolean getChecked[] = mCheckStates.get(groupText);
                childViewHolder.mCheckBox.setChecked(getChecked[childPosition]);
            } else {
                boolean getChecked[] = new boolean[getChildrenCount(groupPosition)];
                mCheckStates.put(groupText, getChecked);
                childViewHolder.mCheckBox.setChecked(false);
            }

            childViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    boolean getChecked[] = mCheckStates.get(groupText);
                    getChecked[child_position] = isChecked;
                    mCheckStates.put(groupText, getChecked);

                    if (isChecked) {
                        mObject.addRelationship(groupText, item_id);
                    } else {
                        mObject.removeRelationship(groupText, item_id);
                    }
                }
            });

        } else {

            // If Topic view, handle star & archive logic
            String[] typeStrings = {groupText.toLowerCase().replace("s", ""), mObject.getType()};
            if (Arrays.asList(typeStrings).contains("topic")) {

                childViewHolder.mStar.setVisibility(View.VISIBLE);
                childViewHolder.mStar.setImageResource(mObject.getImageResourceStatus("star", groupText, item_id));

                childViewHolder.mStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mObject.toggleRelationshipStatus("star", groupText, item_id);
                        ImageButton star_button = (ImageButton) view;
                        star_button.setImageResource(mObject.getImageResourceStatus("star", groupText, item_id));
                    }
                });

                childViewHolder.mStar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mObject.toggleRelationshipStatus("star_lock", groupText, item_id);
                        ImageButton star_button = (ImageButton) view;
                        star_button.setImageResource(mObject.getImageResourceStatus("star", groupText, item_id));
                        return true;
                    }
                });

                childViewHolder.mArchive.setVisibility(View.VISIBLE);
                childViewHolder.mArchive.setImageResource(mObject.getImageResourceStatus("archive", groupText, item_id));

                childViewHolder.mArchive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mObject.toggleRelationshipStatus("archive", groupText, item_id);
                        ImageButton archive_button = (ImageButton) view;
                        archive_button.setImageResource(mObject.getImageResourceStatus("archive", groupText, item_id));
                    }
                });

                childViewHolder.mArchive.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mObject.toggleRelationshipStatus("archive_lock", groupText, item_id);
                        ImageButton archive_button = (ImageButton) view;
                        archive_button.setImageResource(mObject.getImageResourceStatus("archive", groupText, item_id));

                        return true;
                    }
                });
            }
        }
            return convertView;
    }

}
