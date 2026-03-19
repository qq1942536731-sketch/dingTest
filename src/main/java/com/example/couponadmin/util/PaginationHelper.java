package com.example.couponadmin.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

public final class PaginationHelper {
    private PaginationHelper() {
    }

    public static <T> Page<T> paginate(List<T> items, int page, int size) {
        int safeSize = size <= 0 ? 10 : Math.min(size, 50);
        int safePage = Math.max(page, 0);
        int fromIndex = safePage * safeSize;
        if (fromIndex >= items.size()) {
            return new PageImpl<T>(Collections.<T>emptyList(), PageRequest.of(safePage, safeSize), items.size());
        }
        int toIndex = Math.min(fromIndex + safeSize, items.size());
        return new PageImpl<T>(items.subList(fromIndex, toIndex), PageRequest.of(safePage, safeSize), items.size());
    }
}
