package org.ofbiz.face.operators;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.util.ConvertUtil;

public class OperatorWorker {

	public static void main(String[] args) throws Exception {
		OperatorWorker xlsMain = new OperatorWorker();

		// xlsMain.saveCheck("E:\\ofbiz-POS\\applications\\face\\webapp\\Face\\upload\\check1416984470501.xlsx");
		 //xlsMain.write("E:\\writecc.xlsx");
	}
	
	public static Set<String> getStoreIdsByPartyId(Delegator delegator, String userLoginId) {
		Set<String> storeIdSet = FastSet.newInstance();
        try {
			List<GenericValue> gvs = delegator.findList("ProductStoreRole",
					EntityCondition.makeCondition("partyId",
							EntityOperator.EQUALS, userLoginId), null, null, null, false);
			for (GenericValue gv : gvs) {
				storeIdSet.add(gv.get("productStoreId").toString());
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return storeIdSet;
	}
	
	public static Boolean isCheck(Delegator delegator, String entityName, 
			String accessNumber, String erpOptSetValueId, String keyDate) {
		List<GenericValue> gvs = null;
		try {
			gvs = delegator.findByAnd(entityName, UtilMisc.toMap("accessNumber", accessNumber,
					"erpOptSetValueId", erpOptSetValueId ,"keyDate",keyDate));
			for (GenericValue gv : gvs) {
				if (gv.get("isChecked").equals("Y")) {
					return true;
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static String write(String outPath, List<GenericValue> gvs)
			throws IOException {
		int size = gvs.size();
		String fileType = outPath.substring(outPath.lastIndexOf(".") + 1,
				outPath.length());

		// 创建工作文档对象
		Workbook wb = null;
		if (fileType.equals("xls")) {
			wb = new HSSFWorkbook();
		} else if (fileType.equals("xlsx")) {
			wb = new XSSFWorkbook();
		} else {
			System.out.println("您的文档格式不正确！");
			return outPath;
		}
		// 创建sheet对象
		Sheet sheet1 = (Sheet) wb.createSheet("sheet1");

		// 创建一行标题
		Row row0 = (Row) sheet1.createRow(0);
		for (int j = 0; j < 9; j++) {
			Cell cell = row0.createCell(j);
			switch (j) {
			case 0:
				cell.setCellValue("门店编码");
				break;
			case 1:
				cell.setCellValue("接入号");
				break;
			case 2:
				cell.setCellValue("销售类型");
				break;
			case 3:
				cell.setCellValue("套餐值");
				break;
			case 4:
				cell.setCellValue("身份证");
				break;
			case 5:
				cell.setCellValue("开户日期");
				break;
			case 6:
				cell.setCellValue("开户");
				break;
			case 7:
				cell.setCellValue("备注");
				break;
			case 8:
				cell.setCellValue("check");
				break;
			}
		}
		// 循环写入行数据
		GenericValue gv = null;
		for (int i = 1; i <= size; i++) {
			Row row = (Row) sheet1.createRow(i);
			// 循环写入列数据
			for (int j = 0; j < 9; j++) {
				Cell cell = row.createCell(j);
				gv = gvs.get(i-1);
				switch (j) {
				case 0:
					cell.setCellValue((String)gv.get("storeId"));
					break;
				case 1:
					cell.setCellValue((String)gv.get("accessNumber"));
					break;
				case 2:
					cell.setCellValue(getProductSalesPolicyNameById(
							(String)gv.get("productSalesPolicyId")));
					break;
				case 3:
					cell.setCellValue(getErpOptSetValueNameById(
							(String)gv.get("erpOptSetValueId")));
					break;
				case 4:
					cell.setCellValue((String)gv.get("identityCard"));
					break;
				case 5:
					cell.setCellValue((String)gv.get("keyDate"));
					break;
				case 6:
					cell.setCellValue(gv.get("openLocation").equals("S") ? "门店" : "总店");
					break;
				case 7:
					cell.setCellValue((String)gv.get("memo"));
					break;
				case 8:
					cell.setCellValue(gv.get("isChecked").equals("Y") ? "是" : "否");
					break;
				}
			}
		}
		// 创建文件流
		OutputStream stream = new FileOutputStream(outPath);
		// 写入数据
		wb.write(stream);
		// 关闭文件流
		stream.close();
		return outPath;
	}

	/*
	 * private void readXls() throws IOException { InputStream is = new
	 * FileInputStream("E:\\operator.xls"); HSSFWorkbook hssfWorkbook = new
	 * HSSFWorkbook(is);
	 * 
	 * // 循环工作表Sheet for (int numSheet = 0; numSheet <
	 * hssfWorkbook.getNumberOfSheets(); numSheet++) { HSSFSheet hssfSheet =
	 * hssfWorkbook.getSheetAt(numSheet); if (hssfSheet == null) { continue; }
	 * 
	 * // 循环行Row for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum();
	 * rowNum++) { HSSFRow hssfRow = hssfSheet.getRow(rowNum); if (hssfRow ==
	 * null) { continue; }
	 * 
	 * // 循环列Cell for (int cellNum = 0; cellNum <= hssfRow.getLastCellNum();
	 * cellNum++) { HSSFCell hssfCell = hssfRow.getCell(cellNum); if (hssfCell
	 * == null) { continue; }
	 * 
	 * System.out.print(getValue(hssfCell)); } System.out.println(); } } }
	 * 
	 * @SuppressWarnings("static-access") private String getValue(HSSFCell
	 * hssfCell) { if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
	 * return String.valueOf(hssfCell.getBooleanCellValue()); } else if
	 * (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) { return
	 * String.valueOf(hssfCell.getNumericCellValue()); } else { return
	 * String.valueOf(hssfCell.getStringCellValue()); } }
	 */

	public static void saveCheck(String path) throws IOException {
		Map<String, Object> map = FastMap.newInstance();
		String filePath = path;
		String fileType = filePath.substring(filePath.lastIndexOf(".") + 1,
				filePath.length());
		//XSSFWorkbook xssfWorkbook = new XSSFWorkbook(filePath);
		InputStream stream = new FileInputStream(filePath);
		Workbook xssfWorkbook = null;
		if (fileType.equals("xls")) {
			xssfWorkbook = new HSSFWorkbook(stream);
		} else if (fileType.equals("xlsx")) {
			xssfWorkbook = new XSSFWorkbook(filePath);
		} else {
			System.out.println("您输入的excel格式不正确");
		}

		// 循环工作表Sheet
		for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
			Sheet xssfSheet = xssfWorkbook.getSheetAt(0);
			// XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
			if (xssfSheet == null) {
				continue;
			}

			// 循环行Row
			for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
				 // XSSFRow xssfRow = xssfSheet.getRow(rowNum);
				Row xssfRow = xssfSheet.getRow(rowNum);
				if (xssfRow == null) {
					continue;
				}

				// 循环列Cell
				for (int cellNum = 0; cellNum <= xssfRow.getLastCellNum(); cellNum++) {
					 // XSSFCell xssfCell = xssfRow.getCell(cellNum);
					Cell xssfCell = xssfRow.getCell(cellNum);
					if (xssfCell == null) {
						continue;
					}
					switch (cellNum) {
					case 0:
						map.put("storeId", getValue(xssfCell));
						break;
					case 1:
						map.put("accessNumber", getValue(xssfCell));
						break;
					case 2:
						map.put("productSalesPolicyId",
								getProductSalesPolicyIdByName(getValue(xssfCell)));
						break;
					case 3:
						map.put("erpOptSetValueId",
								getErpOptSetValueIdByName(getValue(xssfCell)));
						break;
					case 4:
						map.put("identityCard", getValue(xssfCell));
						break;
					case 5:
						map.put("openingDate", ConvertUtil
								.convertStringToTimeStamp(getValue(xssfCell)
										.replaceAll("/", "-")));
						map.put("keyDate", getValue(xssfCell));
						break;
					case 6:
						map.put("openLocation",
								getValue(xssfCell).equals("总部") ? "C" : "S");
						break;
					case 7:
						map.put("memo", getValue(xssfCell));
						break;
					}
					// System.out.print(getValue(xssfCell));
				}
				// System.out.println();
				map.put("isChecked", "N");
				saveEntity("ErpOptBusinessCheck", map);
			}
		}
	}

	public static void saveOperator(String path) throws IOException {
		Map<String, Object> map = FastMap.newInstance();
		String filePath = path;
		String fileType = filePath.substring(filePath.lastIndexOf(".") + 1,
				filePath.length());
		System.out.println(fileType);
		// XSSFWorkbook xssfWorkbook = new XSSFWorkbook(filePath);
		InputStream stream = new FileInputStream(filePath);
		Workbook xssfWorkbook = null;
		if (fileType.equals("xls")) {
			xssfWorkbook = new HSSFWorkbook(stream);
		} else if (fileType.equals("xlsx")) {
			xssfWorkbook = new XSSFWorkbook(filePath);
		} else {
			System.out.println("您输入的excel格式不正确");
		}

		// 循环工作表Sheet
		for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
			Sheet xssfSheet = xssfWorkbook.getSheetAt(0);
			// XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
			if (xssfSheet == null) {
				continue;
			}

			// 循环行Row
			for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
				Row xssfRow = xssfSheet.getRow(rowNum);
				if (xssfRow == null) {
					continue;
				}

				// 循环列Cell
				for (int cellNum = 0; cellNum <= xssfRow.getLastCellNum(); cellNum++) {
					Cell xssfCell = xssfRow.getCell(cellNum);
					if (xssfCell == null) {
						continue;
					}
					switch (cellNum) {
					case 0:
						map.put("storeId", getValue(xssfCell));
						break;
					case 1:
						map.put("sequenceId", getValue(xssfCell));
						break;
					case 2:
						map.put("accessNumber", getValue(xssfCell));
						break;
					case 3:
						map.put("productSalesPolicyId",
								getProductSalesPolicyIdByName(getValue(xssfCell)));
						break;
					case 4:
						map.put("erpOptSetValueId",
								getErpOptSetValueIdByName(getValue(xssfCell)));
						break;
					case 5:
						map.put("identityCard", getValue(xssfCell));
						break;
					case 6:
						map.put("openingDate", ConvertUtil
								.convertStringToTimeStamp(getValue(xssfCell)
										.replaceAll("/", "-")));
						map.put("keyDate", getValue(xssfCell));
						break;
					case 7:
						map.put("openLocation",
								getValue(xssfCell).equals("总部") ? "C" : "S");
						break;
					case 8:
						map.put("memo", getValue(xssfCell));
						break;
					}
				}
				map.put("isChecked", "N");
				saveEntity("ErpOperatorsBusiness", map);
			}
		}
	}

	@SuppressWarnings("static-access")
	public static String getValue(Cell xssfCell) {
		if (xssfCell.getCellType() == xssfCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(xssfCell.getBooleanCellValue());
		} else if (xssfCell.getCellType() == xssfCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(xssfCell)) {
				Date date = HSSFDateUtil.getJavaDate(xssfCell
						.getNumericCellValue());
				SimpleDateFormat dformat = new SimpleDateFormat("yyyy/MM/dd");
				return dformat.format(date);
			} else {
				DecimalFormat df = new DecimalFormat("0");
				return df.format(xssfCell.getNumericCellValue());
			}

		} else {
			return String.valueOf(xssfCell.getStringCellValue());
		}
	}

	public static void saveEntity(String entityName, Map<String, Object> context) {
		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");

		String accessNumber = (String) context.get("accessNumber");
		String erpOptSetValueId = (String) context.get("erpOptSetValueId");
		String isChecked = (String) context.get("isChecked");
		Timestamp openingDate = (Timestamp) context.get("openingDate");
		String strData = ConvertUtil.convertDateToStringD(openingDate);

		if (UtilValidate.isNotEmpty(accessNumber) &&
				UtilValidate.isNotEmpty(erpOptSetValueId) && UtilValidate.isNotEmpty(strData)) {
			Map<String, String> pkMap = UtilMisc.toMap("accessNumber",
					accessNumber, "erpOptSetValueId", erpOptSetValueId, "keyDate",
					strData);

			GenericValue gv = null;
			try {
				gv = delegator.findByPrimaryKey(entityName, pkMap);
				if (UtilValidate.isNotEmpty(gv)) {
					gv.setNonPKFields(context);
					if (UtilValidate.isEmpty(isChecked)) {
						gv.set("isChecked", "N");
					}
					gv.store();
				} else {
					gv = delegator.makeValue(entityName, pkMap);
					gv.setNonPKFields(context);
					if (UtilValidate.isEmpty(isChecked)) {
						gv.set("isChecked", "N");
					}
					gv.create();
				}
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static String getProductSalesPolicyIdByName(String value) {
		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");

		List<GenericValue> gvs = null;
		try {
			gvs = delegator.findByAnd("ErpPolicy", UtilMisc.toMap(
					"erpPolicyName", value));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (UtilValidate.isNotEmpty(gvs)) {
			return gvs.get(0).get("erpPolicyId").toString();
		}
		return "";
	}
	public static String getProductSalesPolicyNameById(String value) {
		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");

		GenericValue gv = null;
		try {
			gv = delegator.findByPrimaryKey("ErpPolicy", UtilMisc.toMap(
					"erpPolicyId", value));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (UtilValidate.isNotEmpty(gv)) {
			return gv.get("erpPolicyName").toString();
		}
		return "";
	}

	public static String getErpOptSetValueIdByName(String value) {
		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");

		List<GenericValue> gvs = null;
		try {
			gvs = delegator.findByAnd("ErpOptSetValue", UtilMisc.toMap(
					"erpOptSetValueName", value));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (UtilValidate.isNotEmpty(gvs)) {
			return gvs.get(0).get("erpOptSetValueId").toString();
		}
		return "";
	}
	public static String getErpOptSetValueNameById(String value) {
		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");

		GenericValue gv = null;
		try {
			gv = delegator.findByPrimaryKey("ErpOptSetValue", UtilMisc.toMap(
					"erpOptSetValueId", value));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (UtilValidate.isNotEmpty(gv)) {
			return gv.get("erpOptSetValueName").toString();
		}
		return "";
	}

}
