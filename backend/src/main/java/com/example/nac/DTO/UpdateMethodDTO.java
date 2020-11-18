package main.java.com.example.nac.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMethodDTO {
    private String methodName;
    private String subjectName;
    private ArrayList<ObjectIdentityDTO> objects;
    private String scName;
    private String abi;
}
