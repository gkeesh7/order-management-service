package GroceryOrderMgmtService.validators;

import GroceryOrderMgmtService.dao.CategoryThresholdDao;
import GroceryOrderMgmtService.dao.CategoryThresholdLocalDao;
import GroceryOrderMgmtService.dto.ItemRequest;
import GroceryOrderMgmtService.dto.OrderRequest;
import GroceryOrderMgmtService.enums.ItemCategory;

import java.util.HashMap;
import java.util.Map;

public class ValidateCategoryThreshold extends OrderValidatorDecorator {
    private CategoryThresholdDao categoryThresholdDao;
    public ValidateCategoryThreshold(IOrderValidator orderValidator){
        super(orderValidator);
        this.categoryThresholdDao = CategoryThresholdLocalDao.getInstance();
    }

    @Override
    public boolean validate(OrderRequest request) {
        if(!super.validate(request))
            return false;

        Map<ItemCategory, Integer> itemCategoryCount = getItemCategoryCountFromReq(request);
        for (ItemCategory itemCategory : itemCategoryCount.keySet()) {
            Integer threshold = categoryThresholdDao.getLimit(request.getDeliveryDate(), itemCategory);
            if(threshold == null || itemCategoryCount.get(itemCategory) >= threshold)
                return false;
        }

        return true;
    }

    private Map<ItemCategory, Integer> getItemCategoryCountFromReq(OrderRequest request) {
        Map<ItemCategory, Integer> itemCategoryCountMap = new HashMap<>();
        for (ItemRequest itemRequest : request.getItems()) {
            Integer itemCountCount = itemCategoryCountMap.getOrDefault(itemRequest.getItemCategory(), 0);
            itemCountCount += itemRequest.getQuantity();

            itemCategoryCountMap.put(itemRequest.getItemCategory(), itemCountCount);
        }
        return itemCategoryCountMap;
    }
}
