package com.example.jorgecaro.info_usuario_jorge_caro.Contacts;

/**
 * Created by jorge caro on 10/22/2017.
 */

public class Contact {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String  email;
    private String name;
    private String number;

    public Contact(String name, String number, String email) {
        this.email = email;
        this.name = name;
        this.number = number;
    }
}
