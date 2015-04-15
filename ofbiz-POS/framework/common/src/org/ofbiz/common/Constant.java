package org.ofbiz.common;


public class Constant {
	 //  FundStatus BackStatus
    public static final String NOTCLEARED = "0";  //未清
    public static final String PARTCLEARED = "1";  //部分已清
    public static final String CLEAR = "2";  //已清 

	//Rtn  
	public static final String OK = "S";  //正确
	public static final String NG = "F";  //错误
	public static final String UNKNOWN = "U"; //未知
	
	//DocStatus draft/confirmation/approve/cancel
	public static final String DRAFT = "0";//草稿
	public static final String VALID = "1";//确定
	public static final String APPROVED = "2";//批准
	public static final String CLEARED = "3";//已清
	public static final String INVALID = "4";//作废
	
	public static final String MOVEMENTYPE = "TC";//冲红退货
	public static final String MOVE = "SZ";//销售出库
	
	public static final String ISSEQ = "Y";// yes
	public static final String NOSEQ = "N";// no
	
	public static final String SALESOUT="SalesOut";//销售
	public static final String RETURNIN="ReturnIn";//退货
	public static final String DELIVERYCOMMAND = "DeliveryCommand";//发货指令头
	public static final String DELIVERYITEMCOMMAND = "DeliveryItemcommand";//发货指令明细
	public static final String RECEIVECOMMAND = "ReceiveCommand";//收货指令单
	public static final String RECEIVEITEMCOMMAND = "ReceiveItemcommand";//收货指令单
	public static final String SALEORDERHEADER = "SaleOrderHeader";//销售指令单
	public static final String SALEORDERDTL = "SaleOrderDtl";//销售指令
	public static final String RETURNORDERHEADER = "ReturnOrderHeader";//退货指令头
	public static final String RETURNORDERDTL = "ReturnOrderDtl";//退货指令明细
	
	public static final String	 PRE = "P";                 //P-预订单
	public static final String	 PRECOLLECTION = "L";       //L-预收单
    public static final String	 BACK = "B";                //B-退订单
    public static final String	 PREREFUND = "U";           //U-订金返还单        
	public static final String	 SALES = "S";               //S-销售订单
	public static final String	 COLLECTION = "C";          //C-收款单
	public static final String	 SALESOUTWHS = "O";         //O-销售出库单
	public static final String	 RETURN = "R";              //R-退货单
	public static final String   REFUND = "F";              //F-退款单
	public static final String   RETURNINWHS = "I";         //I-退货入库单
	public static final String	 DELIVERY = "D";                  //调拨发货单
	public static final String   RECEIVE = "J";                    //调拨收货单
	public static final String   INVENTORY = "H";                  //盘点
	public static final String   WAREHOUSEOUT = "G";               //发货
	public static final String   WAREHOUSEIN = "E";                //收货
	public static final String   WAREHOUSETRANSFER = "Y";          //移库单
	public static final String   WAREHOUSEREPLENISHMENT = "A";     //补货单 

	public static final String   ONE = "1";  //单据同步状态非草稿置1
	
	public static final String   PAYDAY="1"; //日结账
	public static final String   PAYMENT="0"; //中途缴款  
	//判断是否是空
	 public static boolean isNull(Object value) {
		if (value == null) return true;
		if (value.equals("null"))return true;
        if (value instanceof String) {
        	if (value.equals(""))return true;
        }
        return false;
	 }
	 public static boolean isNotNull(Object value){
		 return !isNull(value);
	 }
	
}
