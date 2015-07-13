package com.imooc.guessmusic.model;

public class Song {
	private String mSongName;
	
	
	private String mSongFileName;
	
	private int mNameLength;
	
	public char[] getNameCharacters(){
		
		
		return mSongName.toCharArray();
		
	}

	public String getSongName() {
		return mSongName;
	}

	public void setSongName(String songName) {
		this.mSongName = songName;
		
		this.mNameLength=songName.length();
	}

	public String getSongFileName() {
		return mSongFileName;
	}

	public void setSongFileName(String songFileName) {
		this.mSongFileName = songFileName;
	}

	public int getNameLength() {
		return mNameLength;
	}

}
