package com.fr.demo;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import com.fr.base.FRContext;
import com.fr.base.dav.Env;
public class SaveReportToDatabase {
	public static void main(String[] args) {
		SaveReport();
	}
	private static void SaveReport() {
		try {
			// �������ݿ�
			String driver = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@192.168.100.169:1521:orcl10g";
			String user = "temp";
			String pass = "temp123";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, pass);
			PreparedStatement presmt = conn
					.prepareStatement("insert into report values(?,?)");
			// ������Ҫ��������ģ���ļ�
			Env oldEnv = FRContext.getCurrentEnv();
			String envPath = oldEnv.getPath();
			File cptfile = new File(envPath
					+ "\\reportlets\\gettingstarted.cpt");
			int lens = (int) cptfile.length();
			InputStream ins = new FileInputStream(cptfile);
			// ��ģ�屣�����
			presmt.setString(1, "gettingstarted.cpt"); // ��һ���ֶδ��ģ�����·��
			presmt.setBinaryStream(2, ins, lens); // �ڶ����ֶδ��ģ���ļ��Ķ�������
			presmt.execute();
			conn.commit();
			presmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}