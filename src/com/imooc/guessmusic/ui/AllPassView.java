package com.imooc.guessmusic.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.imooc.guessmusic.R;

public class AllPassView extends Activity {
	private FrameLayout frameLayout;
	// ΢�Ű�ť
	private ImageButton button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_pass_view);
		frameLayout = (FrameLayout) findViewById(R.id.layout_bar_coin);
		frameLayout.setVisibility(View.GONE);
		button = (ImageButton) findViewById(R.id.pass_weixin_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(AllPassView.this,
						"΢�ŵ�API��Ҫ��ˣ����õ��ˣ�����Ҳû���ʺ���������ظ�������������ϵQQ��",
						Toast.LENGTH_LONG).show();
			}
		});
	}

}
