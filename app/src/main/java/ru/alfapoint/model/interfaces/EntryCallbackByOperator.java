package ru.alfapoint.model.interfaces;

public interface EntryCallbackByOperator {
    void onEdit(int pos);
    void onDelete(long pos);
    void onAccept(long pos, Boolean check);
    void onPhoneCall(int pos);
}
