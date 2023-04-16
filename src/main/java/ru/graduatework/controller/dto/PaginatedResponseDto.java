package ru.graduatework.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * ДТО, хранящий возвращаемый из микросервиса ответ в виде списка с информацией о пагинации.
 * @param <T> - ДТО единичного объекта
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Schema(description = "Список объектов для постраничного вывода")
public class PaginatedResponseDto<T> {

    private List<T> result = new ArrayList<>();

    /**
     * Число записей в {@see result}
     */
    private Integer count;

    /**
     * Общее число записей, удовлетворяющих условиям фильтрации, без учёта пагинации
     */
    private Integer totalCount;

}