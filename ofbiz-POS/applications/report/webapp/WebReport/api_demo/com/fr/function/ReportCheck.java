package com.fr.function;

import java.io.File;
import java.util.HashMap;
import com.fr.base.FRContext;
import com.fr.base.core.json.JSONArray;
import com.fr.base.core.json.JSONObject;
import com.fr.base.dav.Env;
import com.fr.report.CellElement;
import com.fr.report.ResultWorkBook;
import com.fr.report.TemplateElementCase;
import com.fr.report.TemplateWorkBook;
import com.fr.report.WorkBook;
import com.fr.report.io.TemplateWorkBookIO;
import com.fr.script.AbstractFunction;

public class ReportCheck extends AbstractFunction {
	private static HashMap wMap = new HashMap();

	public Object run(Object[] args) {
		// ��ȡ��ʽ�еĲ���
		String cptname = args[0].toString(); // ��ȡ��������
		int colnumber = Integer.parseInt(args[2].toString()); // ��ȡ��Ԫ��������
		int rownumber = Integer.parseInt(args[3].toString()); // ��ȡ��Ԫ��������
		// ���巵�ص�ֵ
		Object returnValue = null;
		// ���屨�����л������������ж�ȡ�ı���
		Env oldEnv = FRContext.getCurrentEnv();
		String envPath = oldEnv.getPath();
		try {
			ResultWorkBook rworkbook = null;
			// ��ȡģ��
			TemplateWorkBook workbook = TemplateWorkBookIO
					.readTemplateWorkBook(oldEnv, envPath + "\\reportlets\\"
							+ cptname);
			// ��ȡ��Ҫ���ݸ�����Ĳ����������ֵ����ʽ��[{"name":para1name,"value":para1value},{"name":para2name,"value":para2value},......]
			JSONArray parasArray = new JSONArray(args[1].toString());
			// ��Ҫ�ж��Ƿ���5����ִ�й���
			// ȡ�������resultworkbook;
			Object tempWObj = wMap.get(cptname + parasArray.toString());
			if (tempWObj != null) {
				// ȡ��hashmap���汣���TpObj;
				TpObj curTpObj = (TpObj) tempWObj;

				if ((System.currentTimeMillis() - curTpObj.getExeTime()) < 8000) {
					rworkbook = curTpObj.getRworkbook();
				} else {
					wMap.remove(cptname + parasArray.toString());
				}
			}
			// ���û�����ã���Ҫ����
			if (rworkbook == null) {
				JSONObject jo = new JSONObject();
				// ���屨��ִ��ʱʹ�õ�paraMap,�����������ֵ
				java.util.Map parameterMap = new java.util.HashMap();
				if (parasArray.length() > 0) {
					for (int i = 0; i < parasArray.length(); i++) {
						jo = parasArray.getJSONObject(i);
						parameterMap.put(jo.get("name"), jo.get("value"));
					}
				}
				// ִ�б���
				rworkbook = workbook.execute(parameterMap);
				// ��������
				wMap.put(cptname + parasArray.toString(), new TpObj(rworkbook,
						System.currentTimeMillis()));
			}
			// ��ȡ�������ж�ӦCell��ֵ
			TemplateElementCase rreport = (TemplateElementCase) rworkbook
					.getResultReport(0);
			CellElement ccell = rreport.getCellElement(colnumber, rownumber);
			returnValue = ccell.getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	class TpObj {
		private ResultWorkBook rworkbook = null;
		private long exeTime = System.currentTimeMillis();

		public TpObj(ResultWorkBook rworkbook, long exeTime) {
			this.setRworkbook(rworkbook);
			this.setExeTime(exeTime);
		}

		public ResultWorkBook getRworkbook() {
			return rworkbook;
		}

		public void setRworkbook(ResultWorkBook rworkbook) {
			this.rworkbook = rworkbook;
		}

		public long getExeTime() {
			return exeTime;
		}

		public void setExeTime(long exeTime) {
			this.exeTime = exeTime;
		}
	}
}