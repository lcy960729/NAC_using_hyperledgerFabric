package chaincode.example;

import lombok.*;
import java.util.ArrayList;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyTable {
    private String subjectId;
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
