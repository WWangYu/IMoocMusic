package com.imooc.guessmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.imooc.guessmusic.R;
import com.imooc.guessmusic.data.Const;
import com.imooc.guessmusic.model.IAlertDialogButtonListener;
import com.imooc.guessmusic.model.IWordButtonClickListener;
import com.imooc.guessmusic.model.Song;
import com.imooc.guessmusic.model.WordButton;
import com.imooc.guessmusic.myui.MyGridView;
import com.imooc.guessmusic.util.MyLog;
import com.imooc.guessmusic.util.MyPlayer;
import com.imooc.guessmusic.util.Util;

public class MainActivity extends Activity implements IWordButtonClickListener {

	public final static String TAG = "MainActivity";
	/** �𰸵�״̬ ��ȷ */
	public final static int STATUS_ANSWER_RIGHT = 1;
	/** �𰸵�״̬ ���� */
	public final static int STATUS_ANSWER_WRONG = 2;
	/** �𰸵�״̬ ������ */
	public final static int STATUS_ANSWER_LACK = 3;

	// ��˸�Ĵ���
	public final static int SPASH_TIMES = 6;

	public final static int ID_DIALOG_DELETE_WORD = 1;
	public final static int ID_DIALOG_TIP_ANSWER = 2;
	public final static int ID_DIALOG_LACK_COINS = 3;

	private Animation mPanAnim;
	private LinearInterpolator mPanLin;

	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;

	private ImageView mViewPan;
	private ImageView mViewPanbar;

	private TextView mCurrentStageView;
	private TextView mCurrentStagePassView;

	private TextView mCurrentSongNamePassView;
	private View mPassView;
	private boolean mIsRunning = false;
	private ImageButton mBtnPlayStart;
	// ���ֿ�����
	private ArrayList<WordButton> mAllWords;

	private ArrayList<WordButton> mBtnSelectWords;

	private MyGridView mMyGridView;

	private LinearLayout mViewWordsContainer;

	private Song mCurrentSong;

	private int mCurrentStageIndex = -1;

	// ��ǰ��ҵ�����
	private int mCurrentCoins = Const.TOATL_COINS;
	// �������ӽ�ҵ�����
	private static final int mPassMoney = 30;
	// ��ҵ�View
	private TextView mViewCurrentCoins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		int[] datas = Util.loadData(this);
		mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];

		mViewPan = (ImageView) findViewById(R.id.imageView3);
		mViewPanbar = (ImageView) findViewById(R.id.imageView4);

		mMyGridView = (MyGridView) findViewById(R.id.gridview);

		mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins + "");

		mMyGridView.registOnWordButtonClick(this);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);

		// ��ʼ������
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
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

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
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

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}
		});

		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mBtnPlayStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handlePlayButton();
			}
		});
		initCurrentStageData();
		handleDeleteWord();
		handleTipAnswer();

	}

	private void clearTheAnswer(WordButton wordButton) {
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisiable = false;
		// wordButton.mViewButton.setTextColor(Color.WHITE);
		// ���ô�ѡ��ɼ���
		setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);

	}

	@Override
	public void onWordButtonClick(WordButton wordButton) {

		// Toast.makeText(this, wordButton.mIndex + "  " +
		// wordButton.mWordString,
		// Toast.LENGTH_SHORT).show();
		setSelectWord(wordButton);

		int checkResult = checkTheAnswer();
		if (checkResult == STATUS_ANSWER_RIGHT) {
			handlePassEvent();
		} else if (checkResult == STATUS_ANSWER_WRONG) {
			sparkTheWrods();
		} else if (checkResult == STATUS_ANSWER_LACK) {
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
		}
	}

	/**
	 * ������ؽ��漰�¼�
	 */
	private void handlePassEvent() {
		handleCoins(mPassMoney);
		mPassView = (LinearLayout) this.findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);
		mPassView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		// ֹͣ����
		mViewPan.clearAnimation();
		MyPlayer.stopTheSong(MainActivity.this);
		// ��ǰ�ص�����
		MyPlayer.playTong(MainActivity.this, MyPlayer.INDEX_STONE_CION);
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);

		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText((mCurrentStageIndex + 1) + "");
		}
		// ��ʾ����������
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}

		// ��һ�ذ�������
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (judegApppassed()) {
					// ���뵽ͨ�ؽ���
					Util.startActivity(MainActivity.this, AllPassView.class);
				} else {
					// ��ʼ��һ��
					mPassView.setVisibility(View.GONE);
					// ���عؿ�����
					initCurrentStageData();
				}

			}
		});
	}

	/**
	 * �ж��Ƿ�ͨ��
	 * 
	 * @return
	 */
	private boolean judegApppassed() {
		return (mCurrentStageIndex == Const.SONG_INFO.length - 1);

	}

	private void setSelectWord(WordButton wordButton) {

		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				mBtnSelectWords.get(i).mViewButton
						.setText(wordButton.mWordString);
				mBtnSelectWords.get(i).mIsVisiable = true;
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;

				MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");

				setButtonVisiable(wordButton, View.INVISIBLE);

				break;
			}

		}
	}

	private void setButtonVisiable(WordButton button, int visibility) {
		button.mViewButton.setVisibility(visibility);
		button.mIsVisiable = (visibility == View.VISIBLE) ? true : false;

		MyLog.d(TAG, button.mIsVisiable + "");
	}

	private void handlePlayButton() {
		if (mViewPanbar != null) {
			if (!mIsRunning) {
				mIsRunning = true;

				// ��ʼ���˽��붯��
				mViewPanbar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);

				MyPlayer.playSong(MainActivity.this,
						mCurrentSong.getSongFileName());

			}
		}
	}

	@Override
	protected void onPause() {
		Util.saveData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);
		mViewPan.clearAnimation();
		MyPlayer.stopTheSong(MainActivity.this);
		super.onPause();

	}

	private Song loadStageSongInfo(int stageIndex) {
		Song song = new Song();

		String[] stage = Const.SONG_INFO[stageIndex];

		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);

		return song;

	}

	/**
	 * ���ص�ǰ�ص�����
	 */
	private void initCurrentStageData() {

		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);

		mBtnSelectWords = initWordSelect();

		LayoutParams params = new LayoutParams(-2, -2);

		// ���ԭ���Ĵ�
		mViewWordsContainer.removeAllViews();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
		if (mCurrentStageView != null) {
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}

		mAllWords = initAllWord();

		mMyGridView.updateData(mAllWords);
		handlePlayButton();
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
		// ��ȡ������ֲ���������

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
	 * �����������
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
			e.printStackTrace();
		}
		return str.charAt(0);
	}

	private int checkTheAnswer() {

		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				return STATUS_ANSWER_LACK;
			}

		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sb.append(mBtnSelectWords.get(i).mWordString);
		}
		return (sb.toString().equals(mCurrentSong.getSongName())) ? STATUS_ANSWER_RIGHT
				: STATUS_ANSWER_WRONG;

	}

	/**
	 * 
	 * ������˸
	 * 
	 */
	private void sparkTheWrods() {
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSpeadTimes = 0;

			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						if (++mSpeadTimes > SPASH_TIMES) {
							return;
						}
						// ִ����˸�߼�
						for (int i = 0; i < mBtnSelectWords.size(); i++) {
							mBtnSelectWords.get(i).mViewButton
									.setTextColor(mChange ? Color.RED
											: Color.WHITE);
						}
						mChange = !mChange;
					}
				});
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1, 150);
	}

	/**
	 * ��ʾ����
	 */
	private void tipAnswer() {

		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				if (!handleCoins(-getTipCoins())) {

					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				onWordButtonClick(findIsAnswerWord(i));

				tipWord = true;
				break;
			}
		}

		if (!tipWord) {
			sparkTheWrods();
		}
	}

	/**
	 * �������ļ����ȡɾ��������Ҫ�õĽ��
	 * 
	 * @return
	 */
	private int getDeletWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
	 * �������ļ����ȡ��ʾ������Ҫ�õĽ��
	 * 
	 * @return
	 */
	private int getTipCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
	 * ����ɾ����ѡ�����¼�
	 */
	private void handleDeleteWord() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_delete_word);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// deleteOneWord();
				showConfirmDialog(ID_DIALOG_DELETE_WORD);
			}
		});
	}

	/**
	 * ɾ������
	 */
	private void deleteOneWord() {
		// ���ٽ��
		if (!handleCoins(-getDeletWordCoins())) {
			// ��Ҳ��� ��ʾ�Ի���
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}
		// ����button���ɼ�

		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}

	/**
	 * �Ҵ�һ�����Ǵ𰸵��ļ������ҵ�ǰ�ǿɼ���
	 * 
	 */
	private WordButton findNotAnswerWord() {
		Random random = new Random();
		WordButton buf = null;
		while (true) {
			int index = random.nextInt(MyGridView.COUNT_WORDS);
			buf = mAllWords.get(index);
			if (buf.mIsVisiable && !isTheAnswerWord(buf)) {
				return buf;
			}

		}

	}

	/**
	 * �ҵ�һ��������
	 * 
	 * @param index
	 * @return
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton buf = null;
		for (int i = 0; i < MyGridView.COUNT_WORDS; i++) {
			buf = mAllWords.get(i);
			if (buf.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[index])) {
				return buf;
			}

		}
		return null;
	}

	/**
	 * �ж������Ƿ��Ǵ�
	 */
	private boolean isTheAnswerWord(WordButton word) {
		boolean result = false;
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			if (word.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[i])) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * ���ӻ��߼����ƶ������Ľ��
	 * 
	 * @param data
	 * @return true ���ӻ��߼��ٳɹ���false ʧ��
	 */
	private boolean handleCoins(int data) {
		// �жϵ�ǰ�ܵĽ�������Ƿ�ɱ�����
		if (mCurrentCoins + data >= 0) {
			mCurrentCoins += data;
			mViewCurrentCoins.setText(mCurrentCoins + "");
			return true;
		} else {

			return false;
		}
	}

	/**
	 * ������ʾ�����¼�
	 */
	private void handleTipAnswer() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// tipAnswer();
				showConfirmDialog(ID_DIALOG_TIP_ANSWER);
			}
		});
	}

	private IAlertDialogButtonListener mBtnOkDeletWordListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			deleteOneWord();
		}
	};

	private IAlertDialogButtonListener mBtnOkTipAnswerListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			tipAnswer();
		}
	};

	private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			Toast.makeText(MainActivity.this, "û���̵ꡣ������Ҫ���ٽ���Ҹ��㣡",
					Toast.LENGTH_SHORT).show();
		}
	};

	// ��ʾ�Ի���
	private void showConfirmDialog(int id) {
		switch (id) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���" + getDeletWordCoins()
					+ "�����ȥ��һ������𰸣�", mBtnOkDeletWordListener);
			break;
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���" + getTipCoins()
					+ "����һ��һ��������ʾ��", mBtnOkTipAnswerListener);
			break;
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "��Ҳ���,ȥ�̵겹�䣿",
					mBtnOkLackCoinsListener);
			break;
		}
	}
}
