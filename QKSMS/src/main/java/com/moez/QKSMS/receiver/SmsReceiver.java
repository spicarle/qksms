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
        long threadId = transaction != null ? conversation.getThreadId() : 0;
        Message message = new Message("Auto reply "+body,address);

        getGroups(context);
        transaction.sendNewMessage(message,threadId);
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
            s.add(Id);
            gC.moveToNext();
        }

        Log.d("getContent", String.valueOf(s.size()));
    }

}

