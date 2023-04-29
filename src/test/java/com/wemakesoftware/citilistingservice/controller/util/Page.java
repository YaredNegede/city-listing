package com.wemakesoftware.citilistingservice.controller.util;

import com.wemakesoftware.citilistingservice.dto.CityDto;
import lombok.Data;

import java.util.ArrayList;

@Data
public class Page {
    public ArrayList<CityDto> content;
    public String pageable;
    public boolean last;
    public int totalPages;
    public int totalElements;
    public boolean first;
    public int size;
    public int number;
    public int numberOfElements;
    public boolean empty;
    public Sort sort;
    public class Sort{
        public boolean sorted;
        public boolean empty;
        public boolean unsorted;
    }
}

