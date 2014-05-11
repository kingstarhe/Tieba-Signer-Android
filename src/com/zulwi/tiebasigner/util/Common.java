package com.zulwi.tiebasigner.util;

import com.zulwi.tiebasigner.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Common {
	public static Dialog createLoadingDialog(Context context, String msg, boolean cancelable) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading);
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
		Window window = loadingDialog.getWindow();
		window.setBackgroundDrawableResource(R.drawable.transparent);
		window.setGravity(Gravity.CENTER | Gravity.BOTTOM);
		loadingDialog.setCancelable(cancelable);
		loadingDialog.setContentView(v);
		return loadingDialog;
	}
}
