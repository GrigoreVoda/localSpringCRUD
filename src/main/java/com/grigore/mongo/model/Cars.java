package com.grigore.mongo.model;




public class Cars {
    private String model;
    private String plateNumber;

    public Cars(){}
    public Cars(String model, String plateNumber) {
        this.model = model;
        this.plateNumber = plateNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
}
