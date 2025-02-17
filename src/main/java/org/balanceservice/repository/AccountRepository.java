package org.balanceservice.repository;

import jakarta.persistence.LockModeType;
import org.balanceservice.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Поиск счета по ID с пессимистичной блокировкой.
     *
     * @param walletId Номер счета
     * @return Счет (если существует)
     */
    @Query("SELECT a FROM AccountEntity a WHERE a.walletId = :walletId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AccountEntity> findByAccountNumberForUpdate(@Param("walletId") Long walletId);

    Optional<AccountEntity> findByWalletId(Long walletId);

    boolean existsByWalletId(Long walletId);
}