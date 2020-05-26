package ru.alfapoint.model.interfaces;

public interface AddServiceCallback {
    void removeService(int servicePosition);
    void addMaster(int position);
    void addCost(String cost,int pos);
    //void checkRasp(int position);
}
