package com.fr.io;
import java.io.File;
import java.io.FileOutputStream;
import com.fr.base.FRContext;
import com.fr.base.ModuleContext;
import com.fr.base.Parameter;
import com.fr.base.dav.LocalEnv;
import com.fr.report.module.EngineModule;
import com.fr.report.page.PageReport;
import com.fr.report.PageWorkBook;
import com.fr.report.TemplateWorkBook;
import com.fr.report.io.ExcelExporter;
import com.fr.report.io.TemplateWorkBookIO;

public class ExportReports {
	public static void main(String[] args) {
		// ���屨�����л���,����ִ�б���
		String envpath = "D:\\FineReport_6.5.6\\WebReport\\WEB-INF";
		FRContext.setCurrentEnv(new LocalEnv(envpath));
		ModuleContext.startModule(EngineModule.class.getName());	
		// ���г����һЩ��Ҫ��ʼ��
		try {
			// δִ��ģ�幤����
			TemplateWorkBook workbook = TemplateWorkBookIO.readTemplateWorkBook(FRContext.getCurrentEnv(),
					"Gettingstarted.cpt");
			// ����ֵΪChina�������������������rworkbook
			Parameter[] parameters = workbook.getParameters();
			java.util.Map parameterMap = new java.util.HashMap();
			for (int i = 0; i < parameters.length; i++) {
				parameterMap.put(parameters[i].getName(), "����");
			}
			PageWorkBook rworkbook = workbook.execute(parameterMap);
			rworkbook.setReportName(0, "����");
			// ���parametermap��������ֵ��Ϊ����,�������ResultReport
			parameterMap.clear();
			for (int i = 0; i < parameters.length; i++) {
				parameterMap.put(parameters[i].getName(), "����");
			}
			PageWorkBook rworkbook2 = workbook.execute(parameterMap);
			PageReport rreport2 = rworkbook2.getPageReport(0);
			rworkbook.addReport("����", rreport2);
			// ���������
			FileOutputStream outputStream;
			// ���������������ΪExcel�ļ�
			outputStream = new FileOutputStream(new File("E:\\ExcelExport.xls"));
			ExcelExporter ExcelExport = new ExcelExporter(null);
			ExcelExport.export(outputStream, rworkbook);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}