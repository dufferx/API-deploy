package org.luismore.hlvsapi.domain.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CombinedEntryTypeCountDTO {
    private List<Integer> graph1Counts;
    private List<Integer> graph2Counts;
    private List<Integer> graph3Counts;
}
