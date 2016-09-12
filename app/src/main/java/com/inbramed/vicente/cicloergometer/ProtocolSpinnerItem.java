package com.inbramed.vicente.cicloergometer;

public class ProtocolSpinnerItem {
    String name;
    String descriptionLoad;
    String descriptionTorque;

    String getName() {return name;}
    String getDescriptionLoad() {return descriptionLoad;}
    String getDescriptionTorque() {return descriptionTorque;}
    void setName(String name) {this.name = name;}

    ProtocolSpinnerItem(String name, String descriptionLoad, String descriptionTorque) {
        this.name = name;
        this.descriptionLoad = descriptionLoad;
        this.descriptionTorque = descriptionTorque;
    }
}
