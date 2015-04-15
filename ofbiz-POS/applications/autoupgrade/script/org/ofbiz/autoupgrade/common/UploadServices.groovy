import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;


public Map upload() {
    LocalDispatcher dispatcher = dctx.getDispatcher();
    Delegator delegator = dctx.getDelegator();
    GenericValue userLogin = (GenericValue) context.get("userLogin");

    ByteBuffer fileData = (ByteBuffer) context.get("uploadFile");
    String fileName = (String) context.get("_uploadFile_fileName");
    String contentType = (String) context.get("_uploadFile_contentType");
    String versionNo = (String) context.get("versionNo");
    String description = (String) context.get("description");
    

    if (UtilValidate.isNotEmpty(fileName)) {
        String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("autoupgrade", "upload.server.path"), context);

		
        File file = new File(imageServerPath + fileName);
        Debug.logInfo("upload file to " + file.getAbsolutePath(), "");

        try {
            RandomAccessFile out = new RandomAccessFile(file, "rw");
            out.write(fileData.array());
            out.close();
        } catch (FileNotFoundException e) {
            Debug.logError(e, "");
            return ServiceUtil.returnError("file can't write, fileName:" + file.getAbsolutePath());
        } catch (IOException e) {
            Debug.logError(e, "");
            return ServiceUtil.returnError("file can't write, fileName:" + file.getAbsolutePath());
        }
        
		GenericValue verItem = delegator.makeValue("VersionAttachment", 
		UtilMisc.toMap("attachmentId", delegator.getNextSeqId("VersionAttachment"),
		"contentType","application/zip;charset=UTF-8",
		"zippath",imageServerPath + fileName,
		"curVersion",versionNo,
		"description",description,
		"status",Long.valueOf(2)));
 		verItem.create();
        

    }

    return ServiceUtil.returnSuccess();
}
