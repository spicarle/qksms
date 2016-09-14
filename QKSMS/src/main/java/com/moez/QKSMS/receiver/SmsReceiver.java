package com.moez.QKSMS.receiver;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.moez.QKSMS.common.vcard.ContactOperations;
import com.moez.QKSMS.data.ContactHelper;
import com.moez.QKSMS.data.ContactList;
import com.moez.QKSMS.data.Conversation;
import com.moez.QKSMS.mmssms.Message;
import com.moez.QKSMS.mmssms.Transaction;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class SmsReceiver extends MessagingReceiver {

    @Override
    public void forwardSms(Context context,String address, String body, long date){
        Log.d("SmsReceiver",address + body );

        Conversation conversation = Conversation.createNew(context);
        Transaction transaction = new Transaction(context);

         getGroups(context);

        for(int i=0;i<2;i++) {
            long threadId = transaction != null ? conversation.getThreadId() : 0;
           // Message message = new Message("Auto reply " + body, new String[]{address});
           //  transaction.sendNewMessage(message, threadId);
            if("J".equals(body.substring(0,1).toUpperCase())){
                Log.e("SMSReceiver","Message joueurs");
            }
            if("D".equals(body.substring(0,1).toUpperCase())){
                Log.e("SMSReceiver","Message dirigeants");
            }
            transaction.sendSmsMessage("Auto reply " + body, new String[]{address}, threadId, 0);
        }
    }

    public void getGroups(Context context){
        final String[] GROUP_PROJECTION = new String[] {
                ContactsContract.Groups._ID, ContactsContract.Groups.TITLE };
        Cursor gC = context.getContentResolver().query(
                ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION,null,null,null);
        gC.moveToFirst();
        LinkedHashSet<String> s = new LinkedHashSet<String>();
        while (!gC.isAfterLast()) {
            int idcolumn = gC.getColumnIndex(ContactsContract.Groups.TITLE);
            String Id = gC.getString(idcolumn);
            int idcolumnGroupId = gC.getColumnIndex(ContactsContract.Groups._ID);
            int groupID = gC.getInt(idcolumnGroupId);
            if( "JOUEURS".equals(Id) || "DIRIGEANTS".equals(Id) )   {
                getSampleContactList(context, groupID);
            }
            s.add(Id);
            gC.moveToNext();
        }

        Log.d("getContent", String.valueOf(s.size()));
    }

    public void getSampleContactList(Context context,int groupID) {

        ArrayList contactList = new ArrayList<ConatctData>();
        Uri groupURI = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID };

        Cursor c = context.getContentResolver().query(
                groupURI,
                projection,
                ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
                        + "=" + groupID, null, null);

        while (c.moveToNext()) {
            String id = c
                    .getString(c
                            .getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));
            Cursor pCur = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[] { id }, null);

            while (pCur.moveToNext()) {
                ConatctData data = new ConatctData();
                data.name = pCur
                        .getString(pCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                data.phone = pCur
                        .getString(pCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.e("SMSReceiver", data.name+ ":" +  data.phone);
                contactList.add(data);
            }

            pCur.close();

        }
    }

    class ConatctData {
        String phone, name;
    }

}

