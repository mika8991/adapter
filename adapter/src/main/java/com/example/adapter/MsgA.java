package com.example.adapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public class MsgA {
    private String msg;
    private String lng;
    private Coordinates coordinates;
}
