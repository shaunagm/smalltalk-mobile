package com.example.android.smalltalk.data;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shauna on 8/27/15.
 */
public class RelatedObjectMap {

    ArrayList<String> header_names;
    HashMap<String, List<String>> child_names;
    HashMap<Integer, boolean[]> child_checked_states;
    HashMap<Integer, List<String>> child_ids;

    public RelatedObjectMap() {

        this.header_names = new ArrayList<String>();
        this.child_names = new HashMap<String, List<String>>();
        this.child_checked_states = new HashMap<Integer, boolean[]>();
        this.child_ids = new HashMap<Integer, List<String>>();

    }

    public void addHeaderNames(String header_name) {
        this.header_names.add(header_name);
    }

    public void addNamesAndIDs(String header_name, Pair names_and_ids) {
        ArrayList<String> child_names = (ArrayList<String>) names_and_ids.first;
        ArrayList<String> child_ids = (ArrayList<String>) names_and_ids.second;
        this.child_names.put(header_name, child_names);
        int integer_index = this.header_names.indexOf(header_name);
        this.child_ids.put(integer_index, child_ids);
    }

    public void addPrecheckedList(String header_name, ArrayList<String> related_names) {
        List<String> all_names = this.child_names.get(header_name);
        boolean[] is_checked_array = new boolean[all_names.size()];
        for(int i =0; i < all_names.size(); i++) {
            if(related_names.contains(all_names.get(i))){
                is_checked_array[i] = true;
            }else{
                is_checked_array[i] = false;
            }
        }
        int integer_index = this.header_names.indexOf(header_name);
        this.child_checked_states.put(integer_index, is_checked_array);
    }

}