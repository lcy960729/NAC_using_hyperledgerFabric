package main.java.com.example.nac.DTO;

import java.time.LocalTime;
import java.util.ArrayList;

public class PolicyTableDTO {
    private String methodName;
    private String resource;
    private String action;
    private String permission;
    private LocalTime toLR;
    private LocalTime timeOfUnBlock;
    private LocalTime minInterval;
    private Integer noFR;
    private Integer Threshold;

    private ArrayList<MisbehaviorTableDTO> misbehaviorTableDTOS;
}
