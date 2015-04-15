package com.fr.io;

import java.io.File;
import java.io.FileOutputStream;
import com.fr.base.FRContext;
import com.fr.web.dav.LocalEnv;
import com.fr.report.ResultWorkBook;
import com.fr.report.WorkBook;
import com.fr.report.io.ExcelExporter;
import com.fr.report.io.TemplateImporter;

public class ExcuteDemo {
	public static void main(String[] args) {
		try {
			// ��ȡģ��
			File cptfile = new File(
					"D:\\FineReport_6.5\\WebReport\\WEB-INF\\reportlets\\doc\\Primary\\Parameter\\Parameter.cpt");
			TemplateImporter tplimp = new TemplateImporter();
			WorkBook workbook = tplimp.generateWorkBook(cptfile);
			/*
			 * ���ɲ���map��ע��������Ӧ��ֵ������ִ�б��� ��ģ����ֻ��һ���������������丳ֵ����
			 * �������ڷ�������ʱ������������ͨ��req.getParameter(name)���
			 * ��õĲ���put��map�У�paraMap.put(paraname,paravalue)
			 */
			java.util.Map paraMap = new java.util.HashMap();
			paraMap.put("����", "����");
			// ������Ҫ����ִ�����ڵĻ���������������ȷ��ȡ���ݿ���Ϣ
			String envPath = "D:\\FineReport_6.5\\WebReport\\WEB-INF";
			FRContext.setCurrentEnv(new LocalEnv(envPath));
			// ʹ��paraMapִ�����ɽ��
			ResultWorkBook result = workbook.execute(paraMap);
			// ʹ�ý���絼����excel
			FileOutputStream outputStream = new FileOutputStream(new File(
					"D:\\Parameter2.xls"));
			ExcelExporter excelExporter = new ExcelExporter();
			excelExporter.export(outputStream, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}