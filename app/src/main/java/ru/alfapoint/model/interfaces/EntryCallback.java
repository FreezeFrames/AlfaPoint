package ru.alfapoint.model.interfaces;

public interface EntryCallback {
    void onPhoneClick(int position);
    void onMapClick(int position);
    void onDelete(int position);
    void onEdit(int position);
}
