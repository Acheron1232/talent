package com.acheron.userserver.api;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ApiUtil {

    private ApiUtil() {
    }

    public static PageRequest pageable(Integer page, Integer size, String sortBy, String sortDir) {
        Sort sort;
        if (sortDir.equalsIgnoreCase("desc")) {
            sort = Sort.by(Sort.Direction.DESC, sortBy);
        } else {
            sort = Sort.by(Sort.Direction.ASC, sortBy);
        }
        return PageRequest.of(page, size, sort);
    }
}
