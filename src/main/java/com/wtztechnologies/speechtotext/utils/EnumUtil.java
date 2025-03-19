package com.wtztechnologies.speechtotext.utils;

import com.wtztechnologies.speechtotext.enums.DocumentType;
import org.hibernate.annotations.SortType;

import java.util.Arrays;

public final class EnumUtil {
    public static boolean checkDocumentType(String documentType) {
        return Arrays.stream(DocumentType.class.getEnumConstants()).anyMatch(r -> r.name().equals(documentType));
    }

    public static boolean checkSortType(String value) {
        return Arrays.stream(SortType.class.getEnumConstants()).anyMatch(r -> r.name().equals(value));
    }
}
