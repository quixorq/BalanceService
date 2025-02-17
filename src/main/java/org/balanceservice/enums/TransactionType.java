package org.balanceservice.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    DEPOSIT("Зачисление средств"),
    WITHDRAWAL("Списание средств"),
    TRANSFER ("Перевод средств");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

}
