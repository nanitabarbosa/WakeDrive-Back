package com.wakedrive.backend.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public final class PageRequests {

    /** Máximo de registros por página: evita que un size enorme obligue al servidor a cargar toda la tabla. */
    public static final int MAX_PAGE_SIZE = 100;

    private PageRequests() {
    }

    public static PageRequest of(int page, int size) {
        return PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
    }

    public static PageRequest of(int page, int size, Sort sort) {
        return PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), sort);
    }
}
