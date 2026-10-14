package com.huayapadev.rentlibraryapi.loan.service;

import com.huayapadev.rentlibraryapi.copy.model.CopyModel;
import com.huayapadev.rentlibraryapi.copy.service.CopyService;
import com.huayapadev.rentlibraryapi.loan.*;
import com.huayapadev.rentlibraryapi.loan.dto.LoanFilterDTO;
import com.huayapadev.rentlibraryapi.loan.dto.LoanResponseDTO;
import com.huayapadev.rentlibraryapi.loan.model.LoanModel;
import com.huayapadev.rentlibraryapi.loan.model.enums.LoanStatusEnum;
import com.huayapadev.rentlibraryapi.shared.exception.InvalidStateException;
import com.huayapadev.rentlibraryapi.shared.exception.ResourceNotFoundException;
import com.huayapadev.rentlibraryapi.user.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    @Value("${app.library.rules.max-active-loans}")
    private int maxActiveLoans;
    private final LoanMapper loanMapper;
    private final LoanRepository loanRepository;
    private final CopyService copyService;

    @Transactional
    public List<LoanResponseDTO> getAll(LoanFilterDTO filter, Pageable pageable) {
        Specification<LoanModel> spec = Specification
            .where(LoanSpecification.hasCopyId(filter.copyId()))
            .and(LoanSpecification.hasUserId(filter.userId()))
            .and(LoanSpecification.hasStatus(filter.status()))
            .and(LoanSpecification.isReturned(filter.isReturned()))
            .and(LoanSpecification.isOverdue(filter.isOverdue()))
            .and(LoanSpecification.loanedAfter(filter.loanedAfter()))
            .and(LoanSpecification.loanedBefore(filter.loanedBefore()));
        return loanRepository.findAll(spec, pageable)
            .stream()
            .map(loanMapper::toResponseDTO)
            .toList();
    }

    @Transactional
    public LoanResponseDTO getById(Long id) {
        LoanModel loan = getEntityById(id);
        return loanMapper.toResponseDTO(loan);
    }

    public LoanModel getEntityById(Long id) {
        return loanRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Loan no encontrada con el id " + id));
    }

    public LoanModel getEntityByIdIgnoringSoftDelete(Long id) {
        return loanRepository.findByIdIgnoringSoftDelete(id)
            .orElseThrow(() -> new ResourceNotFoundException("Loan no encontrado con el id " + id));
    }

    public LoanModel getDeletedById(Long id) {
        LoanModel loan = getEntityByIdIgnoringSoftDelete(id);
        if(loan.getDeletedAt() == null) {
            throw new InvalidStateException("El préstamo con el id " + id + " no esta eliminado");
        }
        return loan;
    }

    @Transactional
    public LoanResponseDTO getMyById(Long userId, Long id) {
        LoanModel loan = loanRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Loan no encontrada con el id " + id));
        return loanMapper.toResponseDTO(loan);
    }

    public LoanModel create(CopyModel copy, UserModel user, Instant dueAt) {
        LoanModel loan = new LoanModel();
        loan.setCopy(copy);
        loan.setUser(user);
        loan.setDueAt(dueAt);
        return loanRepository.save(loan);
    }

    @Transactional
    public LoanResponseDTO updateReturnedAt(Long id) {
        LoanModel loan = getEntityById(id);
        if (loan.getReturnedAt() != null) {
            throw new InvalidStateException("El préstamo ya fue devuelto");
        }
        loan.setReturnedAt(Instant.now());
        loan.setStatus(LoanStatusEnum.RETURNED);

        copyService.markAsAvailable(loan.getCopy().getId());

        LoanModel savedLoan = loanRepository.save(loan);
        return loanMapper.toResponseDTO(savedLoan);
    }

    @Transactional
    public LoanResponseDTO updateDueAt(Long id, Instant dueAt) {
        LoanModel loan = getEntityById(id);
        if(loan.getReturnedAt() != null) {
            throw new InvalidStateException("No se puede extender un préstamo ya devuelto");
        }
        if(dueAt.isBefore(loan.getDueAt())) {
            throw new IllegalArgumentException("La nueva fecha debe ser posterior a la fecha actual de vencimiento");
        }
        loan.setDueAt(dueAt);
        LoanModel savedLoan = loanRepository.save(loan);
        return loanMapper.toResponseDTO(savedLoan);
    }

    public LoanModel cancel(LoanModel loan) {
        loan.setStatus(LoanStatusEnum.CANCELLED);
        return loanRepository.save(loan);
    }

    public void verifyCanBeCancel(LoanModel loan) {
        if(loan.getStatus() == LoanStatusEnum.CANCELLED) {
            throw new InvalidStateException("No se puede cancelar un préstamo ya cancelado");
        }
        if(loan.getStatus() == LoanStatusEnum.RETURNED || loan.getReturnedAt() != null) {
            throw new InvalidStateException("No se puede cancelar un préstamo que fue regresado");
        }
    }

    public LoanModel restore(LoanModel loan) {
        loan.setDeletedAt(null);
        return loanRepository.save(loan);
    }

    public void delete(LoanModel loan) {
        loanRepository.softDeleteById(loan.getId());
    }

    public void verifyCanBeDeleted(LoanModel loan) {
        if(loan.getStatus() == LoanStatusEnum.ACTIVE || loan.getStatus() == LoanStatusEnum.OVERDUE) {
            throw new InvalidStateException("Solo se puede eliminar un préstamo devuelto o cancelado");
        }
    }

    public void verifyNotActiveLoans(Long userId, String extendMessage) {
        boolean hasActiveLoan = loanRepository.existsByUserIdAndStatusActive(userId);
        if(hasActiveLoan) {
            throw new InvalidStateException("Existen prestamos por devolver, " + extendMessage);
        }
    }

    public void verifyCopyNotInUse(Long copyId, String extendMessage) {
        if(loanRepository.existsByCopyIdIgnoringSoftDelete(copyId)) {
            throw new InvalidStateException("La copia ya esta haciendo usada en un préstamo, " + extendMessage);
        }
    }

    public void verifyUserCanBorrow(Long userId) {
        long loansActiveOrOverdue = loanRepository.countActiveOrOverdueByUserId(userId);
        if(loansActiveOrOverdue >= maxActiveLoans) {
            throw new InvalidStateException("Existen prestamos por devolver, Libro pendiente de devolver: " + loansActiveOrOverdue);
        }
    }
}
