package main.java.com.example.nac.Enum;

public enum ChainCode {
    CREATE_LookUpTable("RC", "methodRegister"),
    GET_LookUpTable("RC", "getContract"),
    UPDATE_LookUpTable("RC", "methodUpdate"),
    DELETE_LookUpTable("RC", "methodDelete"),
    GET_LookUpTableList("RC", "getMethodNameList");

    private final String name;
    private final String func;

    public String getName() {
        return name;
    }

    public String getFunc() {
        return func;
    }

    ChainCode(String name, String func) {
        this.name = name;
        this.func = func;
    }
}
