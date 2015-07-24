package com.imooc.guessmusic.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.widget.Toast;

/**
 * “Ù¿÷≤•∑≈¿‡
 * 
 * @author leejian
 * 
 */
public class MyPlayer {
	public final static int INDEX_STONE_ENTER = 0;
	public final static int INDEX_STONE_CANCEL = 1;
	public final static int INDEX_STONE_CION = 2;
	private final static String[] SONG_NAMES = { "enter.mp3", "cancel.mp3",
			"coin.mp3" };
	private static MediaPlayer[] mTongMediaPlayer = new MediaPlayer[SONG_NAMES.length];
	private static MediaPlayer mMusicMediaPlayer;

	public static void playTong(Context context, int index) {
		AssetManager assetManager = context.getAssets();

		if (mTongMediaPlayer[index] == null) {
			mTongMediaPlayer[index] = new MediaPlayer();
			try {
				AssetFileDescriptor fileDescriptor = assetManager
						.openFd(SONG_NAMES[index]);
				mTongMediaPlayer[index].setDataSource(
						fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());
				mTongMediaPlayer[index].prepare();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		mTongMediaPlayer[index].start();

	}

	public static void playSong(Context context, String fileName) {
		if (mMusicMediaPlayer == null) {
			mMusicMediaPlayer = new MediaPlayer();
		}
		mMusicMediaPlayer.reset();

		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
			mMusicMediaPlayer
					.setDataSource(fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());

			mMusicMediaPlayer.prepare();
			mMusicMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stopTheSong(Context context) {
		if (mMusicMediaPlayer != null) {
			mMusicMediaPlayer.stop();
		}
	}
}
