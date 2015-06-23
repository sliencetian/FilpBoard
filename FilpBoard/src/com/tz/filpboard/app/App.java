package com.tz.filpboard.app;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaApplication;
import com.baidu.frontia.api.FrontiaStatistics;
import com.baidu.mobstat.SendStrategyEnum;

public class App extends FrontiaApplication {

	public static final String BAIDU_REPORTID = "5b59e59273";

	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化应用统计
		try {
			initFrontia();
		} catch (Exception e) {
			System.out.println("initFrontia异常");
		}
	}

	private void initFrontia() {
		if (Frontia.init(getApplicationContext(), BAIDU_REPORTID)) {
			FrontiaStatistics stat = Frontia.getStatistics();
			stat.setReportId(BAIDU_REPORTID);
			stat.setSessionTimeout(20);
			stat.enableExceptionLog();
			stat.start(SendStrategyEnum.SET_TIME_INTERVAL, 0, 10, false);
		}
	}
}
