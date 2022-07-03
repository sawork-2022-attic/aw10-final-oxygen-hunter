package com.micropos.delivery.mapper;

import com.micropos.delivery.model.Entry;
import com.micropos.dto.DeliveryEntryDto;
import org.mapstruct.Mapper;

import java.util.Collection;

@Mapper
public interface DeliveryMapper {

    Collection<DeliveryEntryDto> toEntryDtos(Collection<Entry> entries);

    Collection<Entry> toEntries(Collection<DeliveryEntryDto> entryDtos);

    default DeliveryEntryDto toEntryDto(Entry entry) {
        return new DeliveryEntryDto()
                .id(entry.id())
                .orderId(entry.orderId())
                .status(entry.status());
    }

    default Entry toEntry(DeliveryEntryDto entryDto) {
        return new Entry()
                .id(entryDto.getId())
                .orderId(entryDto.getOrderId())
                .status(entryDto.getStatus());
    }
}
