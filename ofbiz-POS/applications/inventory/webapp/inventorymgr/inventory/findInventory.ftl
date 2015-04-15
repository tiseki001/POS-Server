<form method="post" name="findInventory" action="<@ofbizUrl>findInventory</@ofbizUrl>" class="basic-form">
        <table class="basic-table" cellspacing="0">
          <tr>
            <td class="label">编号</td>
            <td><input type="text" name="movementTypeId" value="${uiLabelMap.movementTypeId}"/></td>
          </tr>
          <tr>
            <td class="label">名称</td>
            <td><input type="text" name="movementTypeName" value="物品转移"/></td>
          </tr>
          <tr>
            <td class="label">描述</td>
            <td><input type="text" name="inventoryTransferTypeId" value="物品从A转移到B"/></td>
          </tr>
          <tr>
            <td class="label">起始</td>
            <td><input type="text" name="fromfacilityTypeId" value="A"/></td>
          </tr>
          <tr>
            <td class="label">终点</td>
            <td><input type="text" name="tofaciliTypeId" value="B"/></td>
          </tr>
        </table>
      </form>