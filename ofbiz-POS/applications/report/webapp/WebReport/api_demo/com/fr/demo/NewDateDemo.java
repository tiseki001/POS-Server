package com.fr.demo;

import java.io.File;
import com.fr.base.FRContext;
import com.fr.base.dav.Env;
import com.fr.data.ArrayTableDataDemo;
import com.fr.report.TemplateWorkBook;
import com.fr.report.WorkBook;
import com.fr.report.io.TemplateWorkBookIO;
import com.fr.web.reportlet.Reportlet;
import com.fr.web.request.ReportletRequest;

public class NewDateDemo extends Reportlet {
	public TemplateWorkBook createReport(ReportletRequest reportletrequest) {
		// �½�һ��WorkBook�������ڱ������շ��صı���
		TemplateWorkBook workbook = null;
		Env oldEnv = FRContext.getCurrentEnv();
		String envPath = oldEnv.getPath();
		try {
			// ��ȡģ�壬��ģ�屣��Ϊworkbook���󲢷���
			workbook = TemplateWorkBookIO.readTemplateWorkBook(oldEnv,
					"\\bumen.cpt");
			ArrayTableDataDemo a = new ArrayTableDataDemo(); // ���ö�������ݼ�����
			workbook.putTableData("ds1", a); // ��ģ�帳�µ����ݼ�
		} catch (Exception e) {
			e.getStackTrace();
		}
		return workbook;
	}
}
