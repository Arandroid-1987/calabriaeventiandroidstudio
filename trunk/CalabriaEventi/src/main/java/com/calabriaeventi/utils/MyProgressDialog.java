package com.calabriaeventi.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class MyProgressDialog extends ProgressDialog {
	private Context context;

	public MyProgressDialog(Context context) {
		super(context);
		this.context=context;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Toast.makeText(context,"Clicca di nuovo per tornare al menù", Toast.LENGTH_SHORT).show();
		
	}

}
