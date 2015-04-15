package com.fr.demo;

import com.fr.base.FRContext;
import com.fr.base.dav.Env;
import com.fr.report.TemplateWorkBook;
import com.fr.report.io.TemplateWorkBookIO;
import com.fr.web.reportlet.Reportlet;
import com.fr.web.request.ReportletRequest;

public class SimpleReportletDemo extends Reportlet {
	public TemplateWorkBook createReport(ReportletRequest reportletrequest) {
		// �½�һ��WorkBook�������ڱ������շ��صı���
		Env oldEnv = FRContext.getCurrentEnv();
		TemplateWorkBook WorkBook = null;
		try {
			// ��ȡģ�壬��ģ�屣��Ϊworkbook���󲢷���
			WorkBook = TemplateWorkBookIO.readTemplateWorkBook(oldEnv,
					"\\doc\\Primary\\Parameter\\Parameter.cpt");
		} catch (Exception e) {
			e.getStackTrace();
		}
		return WorkBook;
	}
}
