package com.fr.io;

import java.io.File;
import java.io.FileOutputStream;
import com.fr.base.FRContext;
import com.fr.base.dav.Env;
import com.fr.report.parameter.ReportParameterAttr;
import com.fr.report.WorkBook;
import com.fr.report.io.TemplateExporter;
import com.fr.report.io.TemplateImporter;

public class SetParameterWindow {
	public static void main(String[] args) {
		try {
			// ��ȡģ�屣��ΪWorkBook����
			File cptfile = new File(
					"D:\\FineReport_6.5\\WebReport\\WEB-INF\\reportlets\\doc\\Primary\\Parameter\\Parameter.cpt");
			TemplateImporter tplimp = new TemplateImporter();
			WorkBook workbook = tplimp.generateWorkBook(cptfile);
			// ��ȡWorkBook�������Ĳ�������ReportParameterAttr
			ReportParameterAttr paraAttr = workbook.getReportParameterAttr();
			/*
			 * �����������ʾλ�� 0/ReportParameterAttr.POPUP : ������ʾ
			 * 1/ReportParameterAttr.EMBED : ��Ƕ��ʾ
			 */
			paraAttr.setWindowPosition(ReportParameterAttr.POPUP);
			/*
			 * ����������ѡ�񵯳���ʾ����������õ����Ĳ������ڱ��� String title
			 */
			paraAttr.setParameterWindowTitle("��������parameterAttr��ʹ��");
			// �������ò�������,�������ս��
			workbook.setReportParameterAttr(paraAttr);
			FileOutputStream outputStream = new FileOutputStream(new File(
					"D:\\newParameter.cpt"));
			TemplateExporter templateExporter = new TemplateExporter();
			templateExporter.export(outputStream, workbook);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
