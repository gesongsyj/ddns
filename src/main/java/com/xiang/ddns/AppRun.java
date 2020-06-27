package com.xiang.ddns;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 运行时入口
 * @author xiang
 *
 */
public class AppRun {

	public static void main(String[] args) {
		new Timer("testTimer").schedule(new TimerTask() {
			@Override
			public void run() {
				ddns();
			}
		}, 1000,10000);
	}

	public static void ddns(){
		System.out.println("开始ddns检查");
		UpdateDomainRecord record = new UpdateDomainRecord();
		record.myAnalysisDns();
		System.out.println("ddns运行结束");
	}

}
