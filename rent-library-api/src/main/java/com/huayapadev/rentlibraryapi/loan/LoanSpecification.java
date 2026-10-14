package com.huayapadev.rentlibraryapi.loan;

import com.huayapadev.rentlibraryapi.loan.model.LoanModel;
import com.huayapadev.rentlibraryapi.loan.model.enums.LoanStatusEnum;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class LoanSpecification {
    public static Specification<LoanModel> hasCopyId(Long id) {
        return (root, query, cb)
            -> id == null ? null : cb.equal(root.get("copy").get("id"), id);
    }

    public static Specification<LoanModel> hasUserId(Long id) {
        return (root, query, cb)
            -> id == null ? null : cb.equal(root.get("user").get("id"), id);
    }

    public static Specification<LoanModel> hasStatus(LoanStatusEnum status) {
        return (root, query, cb)
            -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<LoanModel> isReturned(Boolean returned) {
        return (root, query, cb) -> {
            if(returned == null) return null;
            return returned
                ? cb.isNotNull(root.get("returnedAt"))
                : cb.isNull(root.get("returnedAt"));
        };
    }

    public static Specification<LoanModel> isOverdue(Boolean overdue) {
        return (root, query, cb)
            -> overdue == null || !overdue ? null : cb.and(
                cb.isNull(root.get("returnedAt")),
                cb.greaterThan(cb.currentTimestamp(), root.get("dueAt"))
            );
    }

    public static Specification<LoanModel> loanedAfter(Instant from) {
        return (root, query, cb)
            -> from == null ? null : cb.greaterThanOrEqualTo(root.get("loanedAt"), from);
    }

    public static Specification<LoanModel> loanedBefore(Instant to) {
        return (root, query, cb)
            -> to == null ? null : cb.lessThanOrEqualTo(root.get("loanedAt"), to);
    }
}
