package com.xiang.ddns;

/**
 * 运行时入口
 * @author xiang
 *
 */
public class AppRun {

	public static void main(String[] args) {
		System.out.println("开始ddns检查");
		UpdateDomainRecord record = new UpdateDomainRecord();
		record.analysisDns();
		System.out.println("ddns运行结束");
	}

}
