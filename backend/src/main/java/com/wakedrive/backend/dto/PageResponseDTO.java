package com.wakedrive.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {

    private List<T> content;
    private PageMetaDTO page;

    public static <E, T> PageResponseDTO<T> of(Page<E> page, Function<E, T> mapper) {
        PageMetaDTO meta = new PageMetaDTO(
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        List<T> content = page.getContent().stream().map(mapper).toList();
        return new PageResponseDTO<>(content, meta);
    }
}
