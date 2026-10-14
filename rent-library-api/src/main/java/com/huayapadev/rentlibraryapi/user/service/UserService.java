package com.huayapadev.rentlibraryapi.user.service;

import com.huayapadev.rentlibraryapi.auth.service.AuthService;
import com.huayapadev.rentlibraryapi.shared.dto.VerificationResponseDTO;
import com.huayapadev.rentlibraryapi.shared.exception.*;
import com.huayapadev.rentlibraryapi.user.*;
import com.huayapadev.rentlibraryapi.user.dto.UserFilterDTO;
import com.huayapadev.rentlibraryapi.user.dto.UserPatchRequestDTO;
import com.huayapadev.rentlibraryapi.user.dto.UserRequestDTO;
import com.huayapadev.rentlibraryapi.user.dto.UserResponseDTO;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import com.huayapadev.rentlibraryapi.user.model.enums.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final UserMapper userMapper;

    @Transactional
    public List<UserResponseDTO> getAll(UserFilterDTO filter, Pageable pageable) {
        Specification<UserModel> spec = Specification
            .where(UserSpecifications.hasDni(filter.dni()))
            .and(UserSpecifications.hasName(filter.name()))
            .and(UserSpecifications.hasEmail(filter.email()))
            .and(UserSpecifications.hasRole(filter.role()))
            .and(UserSpecifications.isDeleted(filter.isDeleted()));

        return userRepository.findAll(spec, pageable)
            .stream().map(userMapper::toResponseDTO).toList();
    }

    @Transactional
    public UserResponseDTO getById(Long id) {
        UserModel user = getEntityById(id);
        return userMapper.toResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO request) {
        // OPTIMIZE: Aquí creo que sería mejor traer el usuario encontrado y validar su dni y email para reducir a 1 consulta
        if(userRepository.existsByDniIgnoringSoftDelete(request.dni())) {
            throw new AlreadyExistsException("un usuario", "el dni");
        }
        if(userRepository.existsByEmailIgnoringSoftDelete(request.email())) {
            throw new AlreadyExistsException("un usuario", "el correo");
        }
        UserModel user = new UserModel();
        user.setDni(request.dni());
        user.setEmail(request.email());
        user.setName(request.name());
        user.setRole(UserRoleEnum.MEMBER);
        String passwordHash = passwordEncoder.encode(request.password());
        user.setPasswordHash(passwordHash);
        UserModel savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO patch(Long id, UserPatchRequestDTO request) {
        UserModel user = getEntityById(id);
        if(request.name().isPresent()) user.setName(request.name().get());
        UserModel savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }
    
    @Transactional
    public VerificationResponseDTO confirmEmail(Long id, String code) {
        /* TODO(Extra): Esto se encarga de cambiar la contraseña actual por la nueva teniendo en cuenta:
        *   - Buscar el código por id y code
        *   - El código y el nuevo código deben ser iguales
        *   - Cambiar al correo nuevo (Se puede enviar un mensaje al correo anterior de correo cambiado)
        *   - Retornar mensaje
        */
        UserModel user = getEntityById(id);
        authService.revokeAllSessions(user);
        String message = "Código de verificación al nuevo correo";
        return new VerificationResponseDTO(message, Instant.now().plus(Duration.ofMinutes(5)));
    }

    @Transactional
    public UserResponseDTO adminUpdateEmail(
        UserModel authenticatedUser,
        Long id,
        String newEmail,
        boolean revokeSessions
    ) {
        UserModel user = getEntityById(id);
        if(user.getId().equals(authenticatedUser.getId())) {
            throw new InvalidStateException("No se puede actualizar el correo a si mismo");
        }
        if(user.getRole().isHigherOrEqualThan(authenticatedUser.getRole())) {
            throw new InvalidStateException("No puede actualizar un usuario con rol igual o superior");
        }
        if (user.getEmail().equalsIgnoreCase(newEmail)) {
            throw new InvalidStateException("El correo debe ser distinto al actual");
        }
        if (userRepository.existsByEmailIgnoringSoftDelete(newEmail)) {
            throw new AlreadyExistsException("un usuario", "el correo", newEmail);
        }
        user.setEmail(newEmail);
        UserModel savedUser = userRepository.save(user);
        if(revokeSessions) {
            authService.revokeAllSessions(user);
        }
        // TODO(Extra): Notificar al usuario el cambio por el administrador
        return userMapper.toResponseDTO(savedUser);
    }

    // NOTE: Dejado de usar ya que nadie debe cambiar la contraseña de otro usuario
    @Transactional
    public UserResponseDTO adminUpdatePassword(Long id, String password, String newPassword) {
        UserModel user = getEntityById(id);
        authService.validatePassword(user, password);
        if(passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("La contraseña debe ser distinta");
        }
        String passwordHash = passwordEncoder.encode(newPassword);
        user.setPasswordHash(passwordHash);
        UserModel savedUser = userRepository.save(user);
        authService.revokeAllSessions(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO updateRole(UserModel authenticatedUser, Long id, UserRoleEnum role) {
        UserModel user = getEntityById(id);
        if(user.getId().equals(authenticatedUser.getId())) {
            throw new InvalidStateException("No se puede actualizar el rol a si mismo");
        }
        if(user.getRole().isHigherOrEqualThan(authenticatedUser.getRole())) {
            throw new InvalidStateException("No puede actualizar un usuario con rol igual o superior");
        }
        if(user.getRole().equals(role)) {
            throw new InvalidStateException("No se puede actualizar al mismo rol");
        }
        user.setRole(role);
        UserModel savedUser = userRepository.save(user);
        authService.revokeAllSessions(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO restore(Long id) {
        UserModel user = getEntityByIdIgnoringSoftDelete(id);
        if(user.getDeletedAt() == null) {
            throw new InvalidStateException("El usuario con el id " + id + " no está eliminado");
        }
        user.setDeletedAt(null);
        UserModel savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    public UserModel getEntityById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id " + id));
    }

    public UserModel getEntityByIdIgnoringSoftDelete(Long id) {
        return userRepository.findByIdIgnoringSoftDelete(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id " + id));
    }

    public void softDelete(Long userId) {
        userRepository.softDeleteById(userId);
    }
    public void verifyIsMember(UserModel user) {
        if(user.getRole() != UserRoleEnum.MEMBER) {
            throw new InvalidStateException("Solo los miembros pueden reservar los libros");
        }
    }
}
