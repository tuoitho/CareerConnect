package com.careerconnect.service;

import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.util.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaginationService {

//    public <T, E> PaginatedResponse<T> paginate(Page<E> page, Function<E, T> mapper) {
//        List<T> data = page.getContent().stream()
//                .map(mapper)
//                .collect(Collectors.toList());
//        Logger.log("Data: " + data);
//        return new PaginatedResponse<>(
//                page.getNumber(),      // currentPage
//                page.getSize(),        // pageSize
//                page.getTotalPages(),  // totalPages
//                page.getTotalElements(), // totalElements
//                data                   // data
//        );
//    }
    public <T, E> PaginatedResponse<T> paginate(Page<E> page, Function<E, T> mapper) {
        if (page == null || page.getContent().isEmpty()) {
            Logger.log("Page is empty or null.");
            return new PaginatedResponse<>(0, 0, 0, 0, new ArrayList<>());
        }

        List<T> data = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());
        Logger.log("Data: " + data);

        return new PaginatedResponse<>(
                page.getNumber(),      // currentPage
                page.getSize(),        // pageSize
                page.getTotalPages(),  // totalPages
                page.getTotalElements(), // totalElements
                data                   // data
        );
    }

}