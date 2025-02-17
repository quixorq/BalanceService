package org.balanceservice.mapper;

import org.balanceservice.dto.AccountDTO;
import org.balanceservice.dto.BalanceResponseDTO;
import org.balanceservice.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    @Mapping(source = "balance", target = "balance")
    BalanceResponseDTO toBalanceResponseDTO(AccountEntity accountEntity);

    AccountDTO toDTO(AccountEntity account);
}
