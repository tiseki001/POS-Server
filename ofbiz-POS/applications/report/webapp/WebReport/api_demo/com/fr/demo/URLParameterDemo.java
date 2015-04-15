package com.fr.demo;
import com.fr.base.FRContext;  
import com.fr.base.ModuleContext;
import com.fr.report.module.EngineModule;
import com.fr.report.TemplateWorkBook;  
import com.fr.report.io.PDFExporter;
import com.fr.report.io.TemplateWorkBookIO;  
import com.fr.base.Parameter;  
import com.fr.base.dav.Env;  
import com.fr.base.dav.LocalEnv;
import com.fr.web.reportlet.Reportlet;  
import com.fr.web.request.ReportletRequest;  
  
public class URLParameterDemo extends Reportlet {  
    public TemplateWorkBook createReport(ReportletRequest reportletRequest) { 
    	
    	 String envPath="D:\\FineReport_6.5.6\\WebReport\\WEB-INF";  
         FRContext.setCurrentEnv(new LocalEnv(envPath));  
         ModuleContext.startModule(EngineModule.class.getName()); 
        // ��ȡ�ⲿ�����Ĳ���  
    	TemplateWorkBook wbTpl = null;
        String countryValue = reportletRequest.getParameter("����").toString();  
        try {  
            wbTpl = TemplateWorkBookIO.readTemplateWorkBook(  
                    FRContext.getCurrentEnv(), "\\doc\\Primary\\Parameter\\Parameter.cpt");  
            // ��ȡ��������飬����ԭģ��ֻ��countryһ�����������ֱ��ȡindexΪ0�Ĳ����������ⲿ�����ֵ�����ò���  
            Parameter[] ps = wbTpl.getParameters();  
            ps[0].setValue(countryValue);  
            // ԭģ�嶨���в������棬�����Ѿ����ⲿ��ã�ȥ������ҳ��  
            // �����뱣���������棬��ģ������Ϊ���ӳٱ���չʾ���ٴ��������ֱ�Ӹ��ݲ���ֵ��ʾ�����������Ҫ�ٴε����ѯ��ť  
            wbTpl.getReportParameterAttr().setParameterUI(null);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
        return wbTpl; 
    }  
    
}  