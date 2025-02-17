package org.balanceservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "wallet_id")
    private Long walletId;

    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    @Column(name = "version")
    private Long version;

    @OneToMany(mappedBy = "sourceAccountEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TransactionEntity> sourceTransactions = new HashSet<>();

    @OneToMany(mappedBy = "targetAccountEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TransactionEntity> targetTransactions = new HashSet<>();

}