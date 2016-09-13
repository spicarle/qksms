package com.moez.QKSMS.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.moez.QKSMS.data.Conversation;
import com.moez.QKSMS.mmssms.Message;
import com.moez.QKSMS.mmssms.Transaction;

public class SmsReceiver extends MessagingReceiver {

    @Override
    public void forwardSms(Context context,String address, String body, long date){
        Log.d("SmsReceiver",address + body );

        Conversation conversation = Conversation.createNew(context);
        Transaction transaction = new Transaction(context);
        long threadId = transaction != null ? conversation.getThreadId() : 0;
        Message message = new Message("Auto reply "+body,address);

        transaction.sendNewMessage(message,threadId);
    }
}

