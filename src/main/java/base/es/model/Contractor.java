package base.es.model;

import lombok.Data;

@Data
public class Contractor {
    private String INN;
    private String KPP;
    private String phone;

    private String code1;
    private String name;
    private String nameWorking;
    private String nameByDocuments;
    private String legalAddress;
    private String actualAddress;
    private String mainBankAccount;

    private String code2;
    private String shortName;
    private String ownershipType;
    private String fullName;
    private String address;
    private String holding;
}
