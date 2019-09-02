package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason;

public class FetchContactsUseCase {

    public interface Listener {
        void onContactsFetched(List<Contact> contacts);

        void onContactsFetchedFailure();
    }

    private final List<Listener> mListeners = new ArrayList<>();

    private final GetContactsHttpEndpoint mGetContactsHttpEndpoint;

    public FetchContactsUseCase(GetContactsHttpEndpoint mGetContactsHttpEndpoint) {
        this.mGetContactsHttpEndpoint = mGetContactsHttpEndpoint;
    }

    public void fetchContactsAndNotify(String filterTerm) {
        mGetContactsHttpEndpoint.getContacts(filterTerm, new Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contactSchemes) {
                for (Listener listener : mListeners)
                    listener.onContactsFetched(contactsFromSchema(contactSchemes));
            }

            @Override
            public void onGetContactsFailed(FailReason failReason) {
                if (failReason.equals(FailReason.GENERAL_ERROR) || failReason.equals(FailReason.NETWORK_ERROR))
                    for (Listener listener : mListeners)
                        listener.onContactsFetchedFailure();
            }
        });
    }

    private List<Contact> contactsFromSchema(List<ContactSchema> contactSchemes) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactSchema contactSchema : contactSchemes)
            contacts.add(new Contact(
                    contactSchema.getId(),
                    contactSchema.getFullName(),
                    contactSchema.getImageUrl()));

        return contacts;
    }

    public void registerListener(Listener mListener) {
        mListeners.add(mListener);

    }

    public void unregisterListener(Listener mListener) {
        mListeners.remove(mListener);
    }

}
