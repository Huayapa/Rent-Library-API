package com.huayapadev.rentlibraryapi.user;

import com.huayapadev.rentlibraryapi.user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long>, JpaSpecificationExecutor<UserModel> {
    @Query(value = "SELECT EXISTS(SELECT 1 FROM users WHERE dni = :dni)", nativeQuery = true)
    Boolean existsByDniIgnoringSoftDelete(@Param("dni") String dni);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)", nativeQuery = true)
    Boolean existsByEmailIgnoringSoftDelete(@Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE id = :id", nativeQuery = true)
    Optional<UserModel> findByIdIgnoringSoftDelete(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE email = :email")
    Optional<UserModel> findByEmail(@Param("email") String email);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE users SET deleted_at = now() WHERE id = :id", nativeQuery = true)
    void softDeleteById(@Param("id") Long id);

}
