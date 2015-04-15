package com.fr.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.fr.report.TemplateWorkBook;
import com.fr.report.WorkBook;
import com.fr.report.io.ExcelReportImporter;
import com.fr.report.io.TemplateWorkBookIO;

public class ExceltoCpt {
	public static void main(String[] args) throws Exception {
		File excelFile = new File("D:\\aa.xls"); // ��ȡEXCEL�ļ�
		FileInputStream a = new FileInputStream(excelFile);
		TemplateWorkBook tpl = new ExcelReportImporter()
				.generateWorkBookByStream(a);
		OutputStream outputStream = new FileOutputStream(
				new File("E:\\abc.cpt")); // ת����cptģ��
		((WorkBook) tpl).export(outputStream);
	}
}
