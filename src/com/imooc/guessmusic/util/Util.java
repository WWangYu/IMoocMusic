package com.imooc.guessmusic.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.imooc.guessmusic.R;
import com.imooc.guessmusic.data.Const;
import com.imooc.guessmusic.model.IAlertDialogButtonListener;

public class Util {
	private static AlertDialog mAlertDialog;

	public static View getView(Context context, int layoutID) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(layoutID, null);
		return layout;

	}

	public static void startActivity(Context context, Class desti) {
		Intent intent = new Intent();
		intent.setClass(context, desti);
		context.startActivity(intent);

		((Activity) context).finish();
	}

	public static void showDialog(final Context context, String message,
			final IAlertDialogButtonListener listener) {
		View dialogView = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				R.style.Theme_Transparent);
		dialogView = getView(context, R.layout.dialog_view);

		ImageButton btnOkView = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_ok);
		ImageButton btnCancelView = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_cancel);

		TextView txtMessageView = (TextView) dialogView
				.findViewById(R.id.text_dialog_message);

		txtMessageView.setText(message);

		btnOkView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mAlertDialog != null) {
					mAlertDialog.cancel();
				}
				if (listener != null) {

					listener.onClick();
					MyPlayer.playTong(context, MyPlayer.INDEX_STONE_ENTER);
				}

			}
		});
		btnCancelView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mAlertDialog != null) {

					mAlertDialog.cancel();
					MyPlayer.playTong(context, MyPlayer.INDEX_STONE_ENTER);
				}

			}
		});
		builder.setView(dialogView);
		mAlertDialog = builder.create();

		mAlertDialog.show();
	}

	/**
	 * 游戏数据保存
	 */
	public static void saveData(Context context, int stageIndex, int coins) {
		FileOutputStream fis = null;
		try {
			fis = context.openFileOutput(Const.FILE_NAME_SAVA_DATA,
					Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fis);

			dos.writeInt(stageIndex);
			dos.writeInt(coins);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 读取游戏数据
	 * 
	 * @param context
	 * @return
	 */
	public static int[] loadData(Context context) {
		FileInputStream fis = null;
		int[] datas = { -1, Const.TOATL_COINS };
		try {
			fis = context.openFileInput(Const.FILE_NAME_SAVA_DATA);
			DataInputStream dis = new DataInputStream(fis);
			datas[Const.INDEX_LOAD_DATA_STAGE] = dis.readInt();
			datas[Const.INDEX_LOAD_DATA_COINS] = dis.readInt();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return datas;
	}
}
