package com.imooc.guessmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.UserDictionary.Words;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.imooc.guessmusic.R;
import com.imooc.guessmusic.data.Const;
import com.imooc.guessmusic.model.IWordButtonClickListener;
import com.imooc.guessmusic.model.Song;
import com.imooc.guessmusic.model.WordButton;
import com.imooc.guessmusic.myui.MyGridView;
import com.imooc.guessmusic.util.Util;

public class MainActivity extends Activity implements IWordButtonClickListener {
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;

	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;

	private ImageView mViewPan;
	private ImageView mViewPanbar;

	private ImageButton mBtnPlayStart;

	private boolean mIsRunning = false;
	// 文字框容器
	private ArrayList<WordButton> mAllWords;

	private ArrayList<WordButton> mBtnSelectWords;

	private MyGridView mMyGridView;

	private LinearLayout mViewWordsContainer;

	private Song mCurrentSong;

	private int mCurrentStageIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mViewPan = (ImageView) findViewById(R.id.imageView3);
		mViewPanbar = (ImageView) findViewById(R.id.imageView4);

		mMyGridView = (MyGridView) findViewById(R.id.gridview);

		mMyGridView.registOnWordButtonClick(this);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);

		// 初始化动画
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mViewPanbar.startAnimation(mBarOutAnim);
			}
		});

		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mViewPan.startAnimation(mPanAnim);
			}
		});

		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setFillAfter(true);
		mBarOutAnim.setInterpolator(mBarOutLin);
		mBarOutAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}
		});

		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mBtnPlayStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				handlePlayButton();
			}
		});
		initCurrentStageData();
	}

	private void clearTheAnswer(WordButton wordButton) {
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisiable = false;
		
		// 设置待选框可见性
		setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);

	}
	@Override
	public void onWordButtonClick(WordButton wordButton) {

		// Toast.makeText(this, wordButton.mIndex + "  " +
		// wordButton.mWordString,
		// Toast.LENGTH_SHORT).show();
		setSelectWord(wordButton);

	}

	private void setSelectWord(WordButton wordButton) {

		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				mBtnSelectWords.get(i).mViewButton
						.setText(wordButton.mWordString);
				mBtnSelectWords.get(i).mIsVisiable = true;
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;

				setButtonVisiable(wordButton, View.INVISIBLE);
				break;
			}
			
		}
	}

	private void setButtonVisiable(WordButton button, int visibility) {
		button.mViewButton.setVisibility(visibility);
		button.mIsVisiable = (visibility == View.VISIBLE) ? true : false;
	}

	private void handlePlayButton() {
		mViewPanbar.startAnimation(mBarInAnim);
		mBtnPlayStart.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mViewPan.clearAnimation();
		super.onPause();

	}

	private Song loadStageSongInfo(int stageIndex) {
		Song song = new Song();

		String[] stage = Const.SONG_INFO[stageIndex];

		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);

		return song;

	}

	private void initCurrentStageData() {

		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);

		mBtnSelectWords = initWordSelect();

		LayoutParams params = new LayoutParams(-2, -2);

		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}
		mAllWords = initAllWord();

		mMyGridView.updateData(mAllWords);
	}

	private ArrayList<WordButton> initAllWord() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		String[] words = generateWords();
		for (int i = 0; i < MyGridView.COUNT_WORDS; i++) {
			WordButton button = new WordButton();

			button.mWordString = words[i];
			data.add(button);
		}
		return data;

	}

	private ArrayList<WordButton> initWordSelect() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			View view = Util.getView(MainActivity.this,
					R.layout.self_ui_gridview_item);
			final WordButton holder = new WordButton();

			holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisiable = false;

			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			holder.mViewButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					clearTheAnswer(holder);
				}
			});
			data.add(holder);
		}

		return data;

	}

	/*
		 
	*/
	private String[] generateWords() {
		Random random = new Random();
		String[] words = new String[MyGridView.COUNT_WORDS];
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";

		}
		// 获取随机数字并存入数组

		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNT_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}
		for (int i = MyGridView.COUNT_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);

			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}

		return words;

	}

	/*
	 * 生成随机汉字
	 */
	private char getRandomChar() {

		String str = "";
		int hightPos;
		int lowPos;

		Random random = new Random();

		hightPos = (179 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));

		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();

		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str.charAt(0);
	}
}
