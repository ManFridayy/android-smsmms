/*
 * Copyright 2013 Jacob Klinker
 * This code has been modified. Portions copyright (C) 2012, ParanoidAndroid Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.klinker.android.send_message;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;

public class SentReceiver extends BroadcastReceiver {

	   @Override 
	   public void onReceive(Context context, Intent intent) {
		   
		   switch (getResultCode())
           {
               case Activity.RESULT_OK:
                   Cursor query = context.getContentResolver().query(Uri.parse("content://sms/outbox"), null, null, null, null);

                   // mark message as sent successfully
                   if (query.moveToFirst())
                   {
                    String id = query.getString(query.getColumnIndex("_id"));
                    ContentValues values = new ContentValues();
                    values.put("type", "2");
                    values.put("read", true);
                    context.getContentResolver().update(Uri.parse("content://sms/outbox"), values, "_id=" + id, null);
                   }

                   query.close();

                   break;
               case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                    query = context.getContentResolver().query(Uri.parse("content://sms/outbox"), null, null, null, null);

                    // mark message as failed and give notification to user to tell them
                    if (query.moveToFirst())
                    {
                        String id = query.getString(query.getColumnIndex("_id"));
                        ContentValues values = new ContentValues();
                        values.put("type", "5");
                        values.put("read", true);
                        context.getContentResolver().update(Uri.parse("content://sms/outbox"), values, "_id=" + id, null);
                    }

                    NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                       .setSmallIcon(R.drawable.ic_alert)
                       .setContentTitle("Error")
                       .setContentText("Could not send message");

                    mBuilder.setAutoCancel(true);
                    long[] pattern = {0L, 400L, 100L, 400L};
                    mBuilder.setVibrate(pattern);
                    mBuilder.setLights(0xFFffffff, 1000, 2000);

                    NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    Notification notification = mBuilder.build();
                    mNotificationManager.notify(1, notification);
                    break;
           case SmsManager.RESULT_ERROR_NO_SERVICE:

                query = context.getContentResolver().query(Uri.parse("content://sms/outbox"), null, null, null, null);

                // mark message as failed
                if (query.moveToFirst())
                {
                    String id = query.getString(query.getColumnIndex("_id"));
                    ContentValues values = new ContentValues();
                    values.put("type", "5");
                    values.put("read", true);
                    context.getContentResolver().update(Uri.parse("content://sms/outbox"), values, "_id=" + id, null);
                }

                break;
           case SmsManager.RESULT_ERROR_NULL_PDU:

                query = context.getContentResolver().query(Uri.parse("content://sms/outbox"), null, null, null, null);

                // mark message failed
                if (query.moveToFirst())
                {
                    String id = query.getString(query.getColumnIndex("_id"));
                    ContentValues values = new ContentValues();
                    values.put("type", "5");
                    values.put("read", true);
                    context.getContentResolver().update(Uri.parse("content://sms/outbox"), values, "_id=" + id, null);
                }
               
                break;
           case SmsManager.RESULT_ERROR_RADIO_OFF:

                query = context.getContentResolver().query(Uri.parse("content://sms/outbox"), null, null, null, null);

                // mark message failed
                if (query.moveToFirst())
                {
                    String id = query.getString(query.getColumnIndex("_id"));
                    ContentValues values = new ContentValues();
                    values.put("type", "5");
                    values.put("read", true);
                    context.getContentResolver().update(Uri.parse("content://sms/outbox"), values, "_id=" + id, null);
                }
               
                break;
           }

           context.sendBroadcast(new Intent("com.klinker.android.send_message.REFRESH"));
	   } 
	}
