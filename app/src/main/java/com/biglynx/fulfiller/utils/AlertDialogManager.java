/**
 * Copyright (C) 2016 BigLynx
 *
 * All rights reserved.
 */
package com.biglynx.fulfiller.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Gravity;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.biglynx.fulfiller.R;


/**
 * @author Ramakrishna
 * @version 2.0
 */

public class AlertDialogManager {

	private static Typeface tf;
	/**
	 * Function to display alert dialog with event
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param positiveButton
	 */

	public static void showAlertOnly(final Context context, String title,
			final String message, String positiveButton) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.YourDialogStyle); // Setting
		tf = Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Display-Semibold.ttf");																// Dialog
																		// Title
		TextView titlee = new TextView(context);

		// You Can Customise your Title here
		titlee.setText(title);
		titlee.setPadding(10, 10, 10, 10);
		titlee.setGravity(Gravity.CENTER);
		titlee.setTextColor(Color.RED);
		titlee.setTextSize(18);
		titlee.setTypeface(tf);
		builder.setCustomTitle(titlee);
		builder.setMessage(message);
		builder.setNegativeButton(positiveButton,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						if(message.equals(context.getResources().getString(R.string.technical_error))){
							System.exit(0);
						}else{
							dialog.cancel();
						}

					}
				});
		// this will solve your error
		AlertDialog alert = builder.create();
		alert.show();
		alert.getWindow().getAttributes();

		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(16);
		textView.setTypeface(tf);
		Button btn1 = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
		btn1.setTextColor(Color.BLACK);
		btn1.setTextSize(16);
	}

	public static void showValidationAlertMessage(Context context,
			String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		tf = Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Display-Semibold.ttf");
		TextView title = new TextView(context);
		// You Can Customise your Title here
		title.setText("ApnaCity");
		title.setPadding(10, 10, 10, 10);
		title.setGravity(Gravity.CENTER);
		title.setTextColor(Color.BLACK);
		title.setTextSize(18);
		title.setTypeface(tf);
		// Setting Dialog Title
		builder.setCustomTitle(title);
		// Setting Dialog Message
		builder.setMessage(message);

		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		// this will solve your error
		AlertDialog alert = builder.create();
		alert.show();
		alert.getWindow().getAttributes();

		TextView textView = (TextView) alert.findViewById(android.R.id.message);
		textView.setTextSize(18);
		textView.setTypeface(tf);
		Button btn1 = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
		btn1.setTextSize(16);
		btn1.setTypeface(tf);
	}

	public static void showAlertWithoutTitle(Context context, String message,
			String positiveButton) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		tf = Typeface.createFromAsset(context.getAssets(), "fonts/Qubo-Light.ttf");
		SpannableString sucmsg = new SpannableString(message);
		sucmsg.setSpan(new TypefaceSpan(tf.toString()), 0, sucmsg.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		alertDialog.setMessage(sucmsg);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButton,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
	}

	public static void showShareOptions(FragmentActivity activity) {

		try {
			//Share text:
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");


			sharingIntent.putExtra(Intent.EXTRA_TEXT,
					"Hey! I am using Apnacity@."
							+ "\n"
							+ "Download Apnacity@:\n"
							+ "https://play.google.com/store/apps?hl=en");
			//http://goo.gl/rXJ397
			activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));

		} catch (Exception e) {
			Toast.makeText(activity,
					"SMS faild, please try again later!",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}


	public static void showShare(FragmentActivity activity,String address) {
		try {
			//Share text:
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			sharingIntent.putExtra(Intent.EXTRA_TEXT,
					"Hey! I am sharing Property address."
							+ address);
			//http://goo.gl/rXJ397
			activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));

		} catch (Exception e) {
			Toast.makeText(activity,
					"SMS faild, please try again later!",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
