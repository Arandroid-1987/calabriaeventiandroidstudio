package com.calabriaeventi.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

public class DialogBuilder {

	public static Dialog createExitDialog(Activity context) {
		String title = "Sei sicuro di voler uscire?";
		MyListener posListener = new MyListener(context);
		posListener.setPositive(true);
		MyListener negListener = new MyListener(context);
		negListener.setPositive(false);
		return createPosNegDialog(context, title, posListener, negListener);
	}

	private static Dialog createPosNegDialog(Activity context, String title,
			OnClickListener posListener, OnClickListener negListener) {
		String posText = "OK";
		String negText = "Annulla";
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setPositiveButton(posText, posListener);
		builder.setNegativeButton(negText, negListener);
		return builder.create();
	}

	private static class MyListener implements DialogInterface.OnClickListener {
		private boolean positive;
		private Activity context;

		public MyListener(Activity context) {
			this.context = context;
			positive = true;
		}

		public void setPositive(boolean positive) {
			this.positive = positive;
		}

		public void onClick(DialogInterface arg0, int arg1) {
			if (positive) {
				Intent intent = new Intent();
				context.setResult(Activity.RESULT_OK, intent);
				context.finish();
			} else {
				arg0.dismiss();
			}

		}

	}

}
