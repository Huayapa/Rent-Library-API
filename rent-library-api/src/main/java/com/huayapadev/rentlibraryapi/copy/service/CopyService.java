package com.huayapadev.rentlibraryapi.copy.service;

import com.huayapadev.rentlibraryapi.book.model.BookModel;
import com.huayapadev.rentlibraryapi.copy.*;
import com.huayapadev.rentlibraryapi.copy.dto.CopyFilterDTO;
import com.huayapadev.rentlibraryapi.copy.dto.CopyPatchRequestDTO;
import com.huayapadev.rentlibraryapi.copy.dto.CopyResponseDTO;
import com.huayapadev.rentlibraryapi.copy.model.CopyModel;
import com.huayapadev.rentlibraryapi.copy.model.enums.CopyStatusEnum;
import com.huayapadev.rentlibraryapi.shared.exception.AlreadyExistsException;
import com.huayapadev.rentlibraryapi.shared.exception.InvalidStateException;
import com.huayapadev.rentlibraryapi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CopyService {
    private final CopyRepository copyRepository;
    private final CopyMapper copyMapper;

    @Transactional
    public List<CopyResponseDTO> getAll(CopyFilterDTO filter, Pageable pageable) {
        Specification<CopyModel> spec = Specification
            .where(CopySpecification.hasBookId(filter.bookId()))
            .and(CopySpecification.hasInventoryCode(filter.inventoryCode()))
            .and(CopySpecification.hasStatus(filter.status()));

        return copyRepository.findAll(spec, pageable)
            .stream().map(copyMapper::toResponseDTO).toList();
    }

    @Transactional
    public CopyResponseDTO getById(Long id) {
        CopyModel copy = getEntityById(id);
        return copyMapper.toResponseDTO(copy);
    }

    @Transactional
    public CopyModel create(String inventoryCode, BookModel book) {
        CopyModel copy = new CopyModel();
        copy.setBook(book);
        copy.setInventoryCode(inventoryCode);
        copy.setStatus(CopyStatusEnum.AVAILABLE);
        return copyRepository.save(copy);
    }

    public void verifyInventoryCodeNotUse(String inventoryCode) {
        Optional<CopyModel> existing = copyRepository.findByInventoryCodeIgnoringSoftDelete(inventoryCode);
        if(existing.isEmpty()) return;

        CopyModel copy = existing.get();
        if(copy.getDeletedAt() == null) {
            throw new AlreadyExistsException("una copia", "el inventorycode", inventoryCode);
        }

        throw new AlreadyExistsException(
            "una copia", "el inventoryCode", inventoryCode,
            "(Pertenece a una copia eliminada)"
        );
    }

    @Transactional
    public CopyResponseDTO patch(Long id, CopyPatchRequestDTO request) {
        CopyModel copy = getEntityById(id);
        if(request.inventoryCode().isPresent()) {
            if(copyRepository.existsByInventoryCodeAndIdNot(request.inventoryCode().get(), id)) {
                throw new AlreadyExistsException("una copia", "el inventoryCode", request.inventoryCode().get());
            }
            copy.setInventoryCode(request.inventoryCode().get());
        }
        if(request.status().isPresent()) {
            copy.setStatus(request.status().get());
        }
        CopyModel savedCopy = copyRepository.save(copy);
        return copyMapper.toResponseDTO(savedCopy);
    }

    @Transactional
    public CopyResponseDTO restore(Long id) {
        CopyModel copy = getEntityByIdIgnoringSoftDelete(id);
        if(copy.getDeletedAt() == null) {
            throw new InvalidStateException("El libro con el id " + id + " no está eliminado");
        }
        copy.setDeletedAt(null);
        CopyModel savedCopy = copyRepository.save(copy);
        return copyMapper.toResponseDTO(savedCopy);
    }

    @Transactional
    public void delete(Long id) {
        copyRepository.softDeleteById(id);
    }

    @Transactional
    public void deleteForce(Long id) {
        copyRepository.deleteById(id);
    }

    public void verifyExistsById(Long id) {
        if(!copyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Copia no encontrada con el id " + id);
        }
    }

    public void verifyExistsByIdIgnoringSoftDelete(Long id) {
        if(!copyRepository.existsByIdIgnoringSoftDelete(id)) {
            throw new ResourceNotFoundException("Copia no encontrada con el id " + id);
        }
    }

    public void markAsAvailable(Long id) {
        CopyModel copy = getEntityById(id);
        if(copy.getBook().getDeletedAt() != null) {
            copy.setDeletedAt(Instant.now());
        }
        copy.setStatus(CopyStatusEnum.AVAILABLE);
        copyRepository.save(copy);
    }

    public void markAsReserved(CopyModel copy) {
        if(copy.getBook().getDeletedAt() != null) {
            copy.setDeletedAt(Instant.now());
        }
        copy.setStatus(CopyStatusEnum.RESERVED);
        copyRepository.save(copy);
    }

    public CopyModel getEntityById(Long id) {
        return copyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Copia no encontrada con el id " + id));
    }

    public CopyModel getEntityByIdIgnoringSoftDelete(Long id) {
        return copyRepository.findByIdIgnoringSoftDelete(id)
            .orElseThrow(() -> new ResourceNotFoundException("Copia no encontrada con el id " + id));
    }

    public CopyModel markAsLoaned(CopyModel copy) {
        copy.setStatus(CopyStatusEnum.LOANED);
        return copyRepository.save(copy);
    }

    public void restoreAllByBookId(Long id) {
        copyRepository.restoreAvailableDamagedOrLostByBookId(id);
    }

    public void softDeleteByBookId(Long id) {
        copyRepository.softDeleteAvailableDamagedOrLostByBookId(id);
    }

    public void verifyIsAvailable(CopyModel copy) {
        if(copy.getStatus() != CopyStatusEnum.AVAILABLE) {
            throw new InvalidStateException("La copia no esta disponible por su estado " + copy.getStatus());
        }
    }
}
