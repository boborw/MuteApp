package com.line.alermapp.database;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Mute implements Parcelable{
	
	private static final String[] DAYS = new String[]{"星期一","星期二","星期三",
		"星期四","星期五","星期六","星期日"};
	
	private int startHour;
	
	private int startMinute;

	private int endHour;
	
	private int endMinute;
	
	private boolean[] repeatDays;
	
	private boolean repeat;
	
	private boolean mute;
	
	private String repeatDaysDesc;
	
	public  static final Parcelable.Creator<Mute> CREATOR = new Parcelable.Creator<Mute>() {

		@Override
		public Mute createFromParcel(Parcel source) {
			return new Mute(source);
		}

		@Override
		public Mute[] newArray(int size) {
			return new Mute[size];
		}
	};  
	
	public Mute(){
		
	}
	
	public Mute(Parcel source){
		this.startHour = source.readInt();
		this.startMinute = source.readInt();
		this.endHour = source.readInt();
		this.endMinute = source.readInt();
		this.repeatDays = new boolean[7];
		source.readBooleanArray(this.repeatDays);
		source.readBooleanArray(new boolean[]{this.mute});
	}
	
	public Mute(int startHour,int startMinute,int endHour,int endMinute
			,boolean[] repeatDays ,boolean mute){
		this.startHour = startHour;
		this.startMinute = startMinute;
		this.endHour = endHour;
		this.endMinute = endMinute;
		setRepeatDays(repeatDays);
		this.mute = mute;
	}
	
	public static String[] getDays() {
		return DAYS;
	}
	
	/**
	 * 由SQLite的数据库查询结果返回一个Mute的List
	 * @param cursor 数据库查询结果
	 * @return
	 */
	public static List<Mute> getMuteList(Cursor cursor){
		List<Mute> mutes = new ArrayList<Mute>();
		
		while(cursor.moveToNext()){
			int startHour = cursor.getInt(cursor.getColumnIndex("start_hour"));
			int startMinute = cursor.getInt(cursor.getColumnIndex("start_minute"));
			int endHour = cursor.getInt(cursor.getColumnIndex("end_hour"));
			int endMinute = cursor.getInt(cursor.getColumnIndex("end_minute"));
			boolean mute = Boolean.valueOf(cursor.getString(cursor.getColumnIndex("mute")));
			String repeatDays = cursor.getString(cursor.getColumnIndex("repeat_days"));
			Mute obj = new Mute(startHour,startMinute,endHour,endMinute,
					stringToBooleanArray(repeatDays),mute);
			mutes.add(obj);
		}
		
		return mutes;
	}
	
	/**
	 * 将string转换成布尔数组，string中的布尔值必须是以,分割
	 * @param source
	 * @return
	 */
	public static boolean[] stringToBooleanArray(String source){
		String[] boolArray = source.split(",");
		boolean[] result = new boolean[boolArray.length];
		
		for(int i = 0; i < boolArray.length ; i++){
			result[i] = Boolean.valueOf(boolArray[i]);
		}
		
		return result;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public int getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
	}

	public boolean[] getRepeatDays() {
		return repeatDays;
	}
	
	/**
	 * 设置服务重复的日期，并将之由原先的从周一开始转换为由周日开始
	 * @param repeatDays
	 */
	public void setRepeatDays(boolean[] repeatDays) {
		
		this.repeat = false; 
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0 ; i < repeatDays.length; i++){
			
			this.repeatDays[(i+1)%7] = repeatDays[i];
			
			if(repeatDays[i]){
				sb.append(DAYS[i].replace("星期","周") + " ");
				this.repeat = true;
			}
		}
		
		this.repeatDaysDesc = sb.toString();
	}
	
	public boolean isRepeat() {
		return repeat;
	}
	
	public boolean isMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}
	
	public String getRepeatDaysString(){
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < repeatDays.length; i++){
			if(repeatDays[i]){
				sb.append(String.valueOf(repeatDays[i]));
				sb.append(",");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 得到重复日期在文字上的描述
	 * @return
	 */
	public String getRepeatDaysDesc(){
	
		return repeatDaysDesc;
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(startHour);
		dest.writeInt(startMinute);
		dest.writeInt(endHour);
		dest.writeInt(endMinute);
		dest.writeBooleanArray(repeatDays);
		dest.writeBooleanArray(new boolean[]{mute});
	}
	
	/**
	 * 使之返回符合SQLite存储规范的参数列表
	 * @return
	 */
	public String[] getParamsString(){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.valueOf(startHour));
		sb.append(",");
		sb.append(String.valueOf(startMinute));
		sb.append(",");
		sb.append(String.valueOf(endHour));
		sb.append(",");
		sb.append(String.valueOf(endMinute));
		sb.append(",");
		sb.append(getRepeatDaysString());
		sb.append(",");
		sb.append(String.valueOf(mute));
		
		return sb.toString().split(",");
	}
}