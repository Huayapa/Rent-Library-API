package com.huayapadev.rentlibraryapi.copy;

import com.huayapadev.rentlibraryapi.copy.model.CopyModel;
import com.huayapadev.rentlibraryapi.copy.model.enums.CopyStatusEnum;
import org.springframework.data.jpa.domain.Specification;

public class CopySpecification {
    public static Specification<CopyModel> hasBookId(Long bookId) {
        return (root, query, cb)
            -> bookId == null ? null : cb.equal(root.get("book").get("id"), bookId);
    }
    public static Specification<CopyModel> hasStatus(CopyStatusEnum status) {
        return (root, query, cb)
            -> status == null ? null : cb.equal(root.get("status"), status);
    }
    public static Specification<CopyModel> hasInventoryCode(String inventoryCode) {
        return (root, query, cb)
            -> inventoryCode == null ? null : cb.like(root.get("inventoryCode"), "%" + inventoryCode + "%");
    }
}
