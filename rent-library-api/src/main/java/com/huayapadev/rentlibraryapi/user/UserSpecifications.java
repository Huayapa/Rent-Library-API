package com.huayapadev.rentlibraryapi.user;

import com.huayapadev.rentlibraryapi.user.model.UserModel;
import com.huayapadev.rentlibraryapi.user.model.enums.UserRoleEnum;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {
    public static Specification<UserModel> hasDni(String dni) {
        return (root, query, cb)
            -> dni == null ? null : cb.equal(root.get("dni"), dni);
    }
    public static Specification<UserModel> hasName(String name) {
        return (root, query, cb)
            -> name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
    public static Specification<UserModel> hasEmail(String name) {
        return (root, query, cb)
            -> name == null ? null : cb.equal(root.get("name"), name);
    }
    public static Specification<UserModel> hasRole(UserRoleEnum role) {
        return (root, query, cb)
            -> role == null ? null : cb.equal(root.get("status"), role);
    }
    public static Specification<UserModel> isDeleted(Boolean isDeleted) {
        return (root, query, cb) -> {
            if(isDeleted == null) return null;
            return isDeleted
                ? cb.isNotNull(root.get("deletedAt"))
                : cb.isNull(root.get("deletedAt"));
        };
    }
}
