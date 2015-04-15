<form action="<@ofbizUrl>UploadAttach</@ofbizUrl>" method="post" enctype="multipart/form-data">
    <table cellspacing="0" class="basic-table">
        <tr>
            <td width="4%" align="left" align="left" valign="top"><b>${uiLabelMap.VersionNO}</b></td>
            <td>&nbsp;</td>
            <td width="16%" valign="top" align="left">
                <input type="text" size="10" name="versionNo" maxlength="10"/>
            </td>

        </tr>
        <tr>
            <td width="4%" align="left" align="left" valign="top"><b>${uiLabelMap.Description}</b></td>
            <td>&nbsp;</td>
            <td width="16%" valign="top" align="left">
                <textarea cols="33" rows="5" name="descriptionS"  maxlength="165"></textarea>
            </td>

        </tr>
        <tr>
            <td width="20%" align="left" valign="top" colspan="4" ><b><input type="file" name="uploadFile"/></b></td>
            <td width="80%" valign="top" colspan="1" >
            <input type="submit" value="${uiLabelMap.UploadAct}"/>
            </td>
        </tr>
    </table>	
</form>
