package com.wtztechnologies.speechtotext.utils;


import com.wtztechnologies.speechtotext.exceptions.SpeechInternalException;
import org.modelmapper.internal.util.Callable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
    public class PageConverter {

        public <T, R> Page<R> convert(Callable<Page<T>> dataSourceCallable, Function<T, R> mappingFunction) {
            try {
                Page<T> page = dataSourceCallable.call();

                return this.convert(page, mappingFunction);
            } catch (Exception ex) {
                throw new SpeechInternalException(ex, "Could not map retrieved Models from DB to DTOs: %s",
                        ex.getMessage());
            }
        }

        public <T, R> Page<R> convert(Page<T> page, Function<T, R> mappingFunction) {
            try {
                List<R> dtoList = page
                        .stream()
                        .map(mappingFunction)
                        .collect(Collectors.toList());

                return new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
            } catch (Exception ex) {
                throw new SpeechInternalException(ex, "Could not map retrieved Models from DB to DTOs: %s",
                        ex.getMessage());
            }
        }
    }
