package org.balanceservice.mapper;

import org.balanceservice.dto.TransactionDTO;
import org.balanceservice.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {

    @Mapping(source = "sourceAccountEntity.id", target = "sourceAccountId")
    @Mapping(source = "targetAccountEntity.id", target = "targetAccountId")
    TransactionDTO toDTO(TransactionEntity transaction);

    List<TransactionDTO> toDTOList(List<TransactionEntity> transactions);

}
