package com.huayapadev.rentlibraryapi.user.model;

import com.huayapadev.rentlibraryapi.auth.model.TokenModel;
import com.huayapadev.rentlibraryapi.user.model.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity(name = "User")
@Table(name = "users")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor
@Getter
@Setter
public class UserModel implements UserDetails {
    // NOTE. Aquí es mejor trabajarlo con un UUID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, columnDefinition = "CHAR(8)", nullable = false)
    private String dni;

    @Column(length = 120, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @ColumnDefault("MEMBER")
    private UserRoleEnum role = UserRoleEnum.MEMBER;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<TokenModel> tokens;

    // Implementaciones de User Detail
    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    @NullMarked
    public String getUsername() {
        return id.toString();
    }

    @Override
    public boolean isEnabled() {
        return deletedAt == null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
