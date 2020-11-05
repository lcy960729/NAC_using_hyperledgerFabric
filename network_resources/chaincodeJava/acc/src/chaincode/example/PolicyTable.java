package chaincode.example;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyTable {
    private Identity object;
    private String resource;
    private String action;
    private Boolean permission;
    private String toLR;
    private String timeOfUnblock;
    private Long minInterval;
    private int noFR;
    private int threshold;

    private ArrayList<MisbehaviorTable> misbehaviorTables;
}
