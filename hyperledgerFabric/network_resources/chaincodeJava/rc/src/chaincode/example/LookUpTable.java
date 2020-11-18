package chaincode.example;

import lombok.*;

import java.util.ArrayList;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LookUpTable {
    private String methodName;
    private Identity subject;
    private ArrayList<ObjectIdentity> objects;
    private String scName;
    private String abi;
}

/*
{
    "methodName" : "network1",
    "subject" : { "name" : "name1", "macAddress" : "0.0.0.0"},
    "objects" : [ { "name" : "obj1", "macAddress" : "0.0.0.0"}, { "name" : "obj2", "macAddress" : "0.0.0.0"} ],
    "scName" : "acc",
    "abi" : "accessControl"
}
 */