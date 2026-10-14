package com.huayapadev.rentlibraryapi.auth.repository;

import com.huayapadev.rentlibraryapi.auth.model.TokenModel;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenModel, Long> {
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Token t SET t.isRevoked = true WHERE t.tokenId = :tokenId")
    void revokeByTokenId(@Param("tokenId") String tokenId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Token t SET t.isRevoked = true WHERE t.user.id = :userId")
    void revokeAllSessionsByUserId(@Param("userId") Long userId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Token t SET t.isRevoked = true WHERE t.tokenId = :tokenId AND t.user.id = :userId")
    int revokeByTokenIdAndUserId(@Param("tokenId") String tokenId, @Param("userId") Long userId);

    @Query("SELECT t FROM Token t WHERE t.tokenId = :tokenId")
    Optional<TokenModel> findByTokenId(@Param("tokenId") String tokenId);

    boolean existsByTokenIdAndIsRevokedTrue(String tokenId);

    List<TokenModel> findByUserAndIsRevokedFalseAndExpiryDateAfter(UserModel user, Instant now);
}