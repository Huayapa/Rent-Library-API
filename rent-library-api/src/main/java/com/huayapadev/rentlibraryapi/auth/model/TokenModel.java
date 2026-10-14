package com.huayapadev.rentlibraryapi.auth.model;

import com.huayapadev.rentlibraryapi.auth.model.enums.TokenTypeEnum;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity(name = "Token")
@Table(name = "tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TokenModel {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false, name = "token_id", length = 36)
    private String tokenId;

    @Column(unique = true, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", length = 50)
    @Builder.Default
    private TokenTypeEnum tokenType = TokenTypeEnum.BEARER;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @CreationTimestamp
    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "is_revoked")
    private boolean isRevoked;

    @Column(name = "device_info")
    private String deviceInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;
}
