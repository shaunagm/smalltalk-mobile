package com.example.android.smalltalk.data;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.smalltalk.DetailActivity;
import com.example.android.smalltalk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Adapted from https://gist.github.com/Psest328/8762232 by shauna on 8/26/15.
 */

public class RelationshipAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private SmalltalkObject mObject;
    private ExpandableListView mListView;
    private boolean mShowArchive;
    private boolean mThroughGroup;

    private List<String> groupNames = new ArrayList<String>();
    private HashMap<String,SmalltalkObject.relatedObjects> mRelatedObjects = new HashMap<>();
    private HashMap<String, Integer> imageIDs = new HashMap<>();

    public RelationshipAdapter(Context context, SmalltalkObject current_object, boolean show_archive,
                               boolean through_group, ExpandableListView listView) {
        mContext = context;
        mObject = current_object;
        mShowArchive = show_archive;
        mThroughGroup = through_group;
        mListView = listView;

        // Define related objects
        if (!(mObject.getType().equals("contact"))) {
            groupNames.add("contact");
            mRelatedObjects.put("contact", mObject.getRelatedObjects("contact", mShowArchive, mThroughGroup));
            imageIDs.put("contact", R.drawable.ic_person_black_48dp);
        }
        if (!(mObject.getType().equals("group"))) {
            groupNames.add("group");
            mRelatedObjects.put("group", mObject.getRelatedObjects("group", mShowArchive, mThroughGroup));
            imageIDs.put("group", R.drawable.ic_people_black_48dp);

        }
        if (!(mObject.getType().equals("topic"))) {
            groupNames.add("topic");
            mRelatedObjects.put("topic", mObject.getRelatedObjects("topic", mShowArchive, mThroughGroup));
            imageIDs.put("topic", R.drawable.topic);
        }
    }


    @Override
    public int getGroupCount() { return groupNames.size(); }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mRelatedObjects.get(groupNames.get(groupPosition)).names.size();
    }

    @Override
    public String getGroup(int groupPosition) { return groupNames.get(groupPosition); }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return mRelatedObjects.get(groupNames.get(groupPosition)).names.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }

    @Override
    public boolean hasStableIds() { return false; }

    public String groupName(GroupViewHolder view) {
        return view.hGroupName.getText().toString().replace("s","");
    }

    public boolean isTopicType(GroupViewHolder group_holder) {
        return (groupName(group_holder).equals("topic") || mObject.getType().equals("topic"));
    }
    
    public boolean isTopicType(String groupName) {
        return (groupName.equals("topic") || mObject.getType().equals("topic"));
    }

    public final class GroupViewHolder {
        TextView hGroupName;
        ImageView hGroupIcon;
        CheckBox hShowArchive;
        CheckBox hShowThrough;
    }

    public final class ChildViewHolder {
        TextView mChildId;
        TextView mChildText;
        TextView mChildDetails;
        ImageButton mStar;
        ImageButton mArchive;
        boolean topicType;
    }

    public void refreshRelatedObjects(String groupName) {
        mRelatedObjects.put(groupName, mObject.getRelatedObjects(groupName, mShowArchive, mThroughGroup));
    }

    // Status should be equal to View.GONE or View.VISIBLE
    public void toggleTopicControls(GroupViewHolder group_holder, int status) {
        if (isTopicType(group_holder)) {
            group_holder.hShowArchive.setVisibility(status);
            if (mObject.getType().equals("contact")) {
                group_holder.hShowThrough.setVisibility(status);
            }
        }
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        final GroupViewHolder group_holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_header, null);
            group_holder = new GroupViewHolder();
            group_holder.hGroupName = (TextView) convertView.findViewById(R.id.header_name);
            group_holder.hGroupIcon = (ImageView) convertView.findViewById(R.id.header_name_icon);
            group_holder.hShowArchive = (CheckBox) convertView.findViewById(R.id.show_archived_relationships);
            group_holder.hShowThrough = (CheckBox) convertView.findViewById(R.id.show_through_relationships);
            convertView.setTag(group_holder);
        }
        else {
            group_holder = (GroupViewHolder) convertView.getTag();
        }

        // Set display fields
        final String groupName = groupNames.get(groupPosition);
        group_holder.hGroupName.setText(groupName + "s");
        group_holder.hGroupIcon.setImageResource(imageIDs.get(groupName));


        // LongClickListener seems to prevent the default click behavior.  We also need to add topic control
        // toggling.
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListView.isGroupExpanded(groupPosition)) {
                    mListView.collapseGroup(groupPosition);
                    toggleTopicControls(group_holder, View.GONE);
                } else {
                    mListView.expandGroup(groupPosition);
                    toggleTopicControls(group_holder, View.VISIBLE);
                }
            }
        });


        group_holder.hShowArchive.setChecked(mShowArchive);
        group_holder.hShowArchive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mShowArchive = isChecked;
                refreshRelatedObjects(groupName(group_holder));
                notifyDataSetChanged();
            }
        });

        group_holder.hShowThrough.setChecked(mThroughGroup);
        group_holder.hShowThrough.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mThroughGroup = isChecked;
                refreshRelatedObjects(groupName(group_holder));
                notifyDataSetChanged();
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                
                AlertDialog.Builder editRelationshipBuilder = new AlertDialog.Builder(mContext);
                editRelationshipBuilder.setTitle("Add and remove " + groupName + " from " + mObject.getName());

                // Get all possible rows (the 0 means don't get related only)
                Cursor cursor = mObject.getConditionalRowsCursor(groupName, false, mShowArchive, mThroughGroup);
                Pair names_and_ids = mObject.getItemInfoPair(cursor);
                final String[] names = (String[]) names_and_ids.first;
                final String[] IDs = (String[]) names_and_ids.second;

                final boolean[] selectedItems = new boolean[names.length];
                refreshRelatedObjects(groupName);

                for (int i = 0; i < names.length; i++) {
                    if (mRelatedObjects.get(groupName).names.contains(names[i])) {
                        selectedItems[i] = true;
                    } else {
                        selectedItems[i] = false;
                    }
                }

                editRelationshipBuilder.setMultiChoiceItems(names, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            mObject.addRelationship(groupName, IDs[i]);
                        } else {
                            mObject.removeRelationship(groupName, IDs[i]);
                        }
                        refreshRelatedObjects(groupName);
                        mListView.collapseGroup(groupPosition);
                    }
                }).setCancelable(true).create().show();

                return true;
            }
        });

//        if (mAdapterType.equals("topics")) {
//            mListView.expandGroup(0);
//            toggleTopicControls(group_holder, View.VISIBLE);
//        }

        return convertView;
    }

    public View newView(boolean useTopicType) {
        ChildViewHolder childViewHolder = new ChildViewHolder();
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView;

        if (useTopicType) {
            convertView = inflater.inflate(R.layout.expandable_list_topic_items, null);
            childViewHolder.mStar = (ImageButton) convertView.findViewById(R.id.star_button);
            childViewHolder.mArchive = (ImageButton) convertView.findViewById(R.id.archive_button);
        } else {
            convertView = inflater.inflate(R.layout.expandable_list_other_items, null);
        }

        childViewHolder.mChildText = (TextView) convertView.findViewById(R.id.expandable_list_item_name);
        childViewHolder.mChildDetails = (TextView) convertView.findViewById(R.id.expandable_list_item_details);
        childViewHolder.mChildId = (TextView) convertView .findViewById(R.id.expandable_list_item_edit_secret_id);
        childViewHolder.topicType = useTopicType;

        convertView.setTag(childViewHolder);
        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        
        // Set some vars
        final String groupName = groupNames.get(groupPosition);
        final SmalltalkObject.relatedObjects relatedObject = mRelatedObjects.get(groupName);
        final boolean useTopicType = isTopicType(groupName);
        
        final String childText = relatedObject.names.get(childPosition);
        final String childDetails = relatedObject.details.get(childPosition);
        final String childID = relatedObject.IDs.get(childPosition);

        // Check for recycled views.  If recycled view is of wrong type, create new view.
        if (convertView == null) {
            convertView = newView(useTopicType);
        }
        ChildViewHolder childViewHolder = (ChildViewHolder) convertView.getTag();
        if (!(useTopicType == childViewHolder.topicType)) {
            convertView = newView(useTopicType);
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        // Set universal view elements
        childViewHolder.mChildText.setText(childText);
        childViewHolder.mChildId.setText(childID);

        childViewHolder.mChildDetails.setEllipsize(TextUtils.TruncateAt.END);
        childViewHolder.mChildDetails.setText(childDetails);
        childViewHolder.mChildDetails.setMaxLines(1);
        if (useTopicType) {
            childViewHolder.mChildDetails.setMaxLines(2);
        }

        // If a group or child view, clicking anywhere on the view goes to that item.  If topic view,
        // only clicking on the Title will bring you there.
        if (useTopicType) {
            childViewHolder.mChildText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class)
                            .putExtra("item_id", childID)
                            .putExtra("item_type", groupName);
                    mContext.startActivity(intent);
                }
            });
        } else {
            View view = (View) childViewHolder.mChildText.getParent();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class)
                            .putExtra("item_id", childID)
                            .putExtra("item_type", groupName);
                    mContext.startActivity(intent);
                }
            });
        }


        if (useTopicType) {

            childViewHolder.mStar.setImageResource(mObject.getImageResourceStatus("star", groupName, childID));
            childViewHolder.mStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mObject.toggleRelationshipStatus("star", groupName, childID);
                    ImageButton star_button = (ImageButton) view;
                    star_button.setImageResource(mObject.getImageResourceStatus("star", groupName, childID));
                }
            });
            childViewHolder.mStar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mObject.toggleRelationshipStatus("star_lock", groupName, childID);
                    ImageButton star_button = (ImageButton) view;
                    star_button.setImageResource(mObject.getImageResourceStatus("star", groupName, childID));
                    return true;
                }
            });

            childViewHolder.mArchive.setImageResource(mObject.getImageResourceStatus("archive", groupName, childID));
            childViewHolder.mArchive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mObject.toggleRelationshipStatus("archive", groupName, childID);
                    ImageButton archive_button = (ImageButton) view;
                    archive_button.setImageResource(mObject.getImageResourceStatus("archive", groupName, childID));
                }
            });
            childViewHolder.mArchive.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mObject.toggleRelationshipStatus("archive_lock", groupName, childID);
                    ImageButton archive_button = (ImageButton) view;
                    archive_button.setImageResource(mObject.getImageResourceStatus("archive", groupName, childID));
                    return true;
                }
            });
        }

        return convertView;
    }

}
