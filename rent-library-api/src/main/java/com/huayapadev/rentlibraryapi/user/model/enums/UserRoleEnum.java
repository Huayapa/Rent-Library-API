package com.huayapadev.rentlibraryapi.user.model.enums;

import com.huayapadev.rentlibraryapi.shared.interfaces.ValuedEnum;
import lombok.RequiredArgsConstructor;

/* NOTE:
*   Para mayor seguridad es mejor tener un SUPERADMIN que se haya creado desde la BD y
*   no se debe poder asignar este rol desde la API y debe tener su propio endpoint en casos de emergencia, como:
*   - Revokar acceso de cualquier usuario
*   - Cambiar el rol a cualquier usuario sin importar su rol
*/
@RequiredArgsConstructor
public enum UserRoleEnum implements ValuedEnum<Integer> {
    ADMIN(3),
    LIBRARIAN(2),
    MEMBER(1);

    private final int level;

    @Override
    public Integer getValue() {
        return level;
    }

    @Override
    public String displayValue() {
        return name();
    }

    public boolean isHigherOrEqualThan(UserRoleEnum otherUserRole) {
        return this.level >= otherUserRole.level;
    }
}
