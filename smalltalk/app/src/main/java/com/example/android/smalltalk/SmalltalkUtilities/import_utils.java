package com.example.android.smalltalk.SmalltalkUtilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shauna on 9/7/15.
 */
public class import_utils {

    static String phoneSelection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
    static String starSelection = ContactsContract.Contacts.STARRED + "=1";
    static String fiveSelection = ContactsContract.Contacts.TIMES_CONTACTED + ">4";

    // Gets cursor for Contacts
    public static Cursor getAndroidContacts(Context context, String type) {
        Uri URI = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
        String selection = "";
        switch(type){
            case "phone":
                selection = phoneSelection;
                break;
            case "star":
                selection = starSelection;
                break;
            case "year":
                Long timestamp = System.currentTimeMillis(); // Current date in milliseconds
                timestamp = timestamp - (365 * 24 * 60 * 60 * 1000);
                selection = ContactsContract.Contacts.LAST_TIME_CONTACTED + ">" + timestamp;
                break;
            case "five":
                selection = fiveSelection;
                break;
        }
        return context.getContentResolver().query(URI, projection, selection, null,  ContactsContract.Contacts.DISPLAY_NAME + " ASC");
      }

    public static String getAndroidContactsCount(Context context, String type) {
        Cursor cursor = getAndroidContacts(context, type);
        return String.valueOf(cursor.getCount());
    }

    // Get contacts from phone
    public static Cursor getPhoneContacts(Context context) {
        Uri URI = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
        return context.getContentResolver().query(URI, projection, selection, null, ContactsContract.Contacts.DISPLAY_NAME);
    }

    // Get groups from phone
    public static Cursor getPhoneGroups(Context context) {
        Uri URI = ContactsContract.Groups.CONTENT_URI;
        String[] projection = new String[] {ContactsContract.Groups._ID, ContactsContract.Groups.TITLE };
        return context.getContentResolver().query(URI, projection, null, null, ContactsContract.Groups.TITLE);
    }

    // Get contact name given contact ID
    public static Cursor getContactNameGivenID(Context context, String contact_id) {
        Uri URI = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
        String selection = ContactsContract.Contacts._ID + "=" + contact_id;
        return context.getContentResolver().query(URI, projection, selection, null, null);
    }

    // Get contact IDs from group
    public static Cursor getContactIDsFromGroup(Context context, String group_id) {
        Uri URI = ContactsContract.Data.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
                ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID};
        String selection = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=" + group_id;
        return context.getContentResolver().query(URI, projection, selection, null, null);
    }

    // Get contact names given a group
    public static String[] getContactNamesGivenGroup(Context context, String group_id) {
        ArrayList<String> contact_names = new ArrayList<String>();
        Cursor cursor = getContactIDsFromGroup(context, group_id);
        while (cursor.moveToNext()) {
            String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));
            Cursor name_cursor = getContactNameGivenID(context, contact_id);
            name_cursor.moveToNext();
            String name = name_cursor.getString(name_cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contact_names.add(name.toLowerCase());
        }
        return contact_names.toArray(new String[contact_names.size()]);
    }
}
