package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    public static final String FILTER_TERM = "filter_term";
    public static final String ID = "id";
    public static final String FULL_NAME = "fullName";
    public static final String FULL_PHONE_NUMBER = "fullPhoneNumber";
    public static final String IMAGE_URL = "imageUrl";
    public static final double AGE = 5.0;
    FetchContactsUseCase SUT;

    @Mock
    GetContactsHttpEndpoint mGetContactsHttpEndpointMock;
    @Mock
    FetchContactsUseCase.Listener mListenerMock1;
    @Mock
    FetchContactsUseCase.Listener mListenerMock2;
    @Captor
    ArgumentCaptor<List<Contact>> mAcContactsList;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchContactsUseCase(mGetContactsHttpEndpointMock);

        success();
    }


    // Correct filterTerm passed to the endpoint

    @Test
    public void fetchContacts_correctFilterTermPassedToEndpoint() throws Exception {
        //Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        //Act
        SUT.fetchContactsAndNotify(FILTER_TERM);
        //Assert
        verify(mGetContactsHttpEndpointMock).getContacts(ac.capture(), any(Callback.class));
        assertThat(ac.getValue(), is(FILTER_TERM));
    }

    // success - all observers notified with correct data

    @Test
    public void fetchContacts_success_observersNotifiedWithCorrectData() throws Exception {
        //Arrange
        //Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        //Assert
        verify(mListenerMock1).onContactsFetched(mAcContactsList.capture());
        verify(mListenerMock2).onContactsFetched(mAcContactsList.capture());
        List<List<Contact>> captures = mAcContactsList.getAllValues();
        List<Contact> capture1 = captures.get(0);
        List<Contact> capture2 = captures.get(1);
        assertThat(capture1, is(getContacts()));
        assertThat(capture2, is(getContacts()));

    }

    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(ID, FULL_NAME, IMAGE_URL));
        return contacts;
    }

    // success - unsubscribed observers not notified
    @Test
    public void fetchContacts_success_unsubscribedObserversNotified() throws Exception {
        //Arrange
        //Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.unregisterListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        //Assert
        verify(mListenerMock1).onContactsFetched(any(List.class));
        verifyNoMoreInteractions(mListenerMock2);

    }

    // general error - observers notified of failure
    @Test
    public void fetchContacts_generalError_notifiedFailure() throws Exception {
        //Arrange
        generalError();
        //Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        //Assert
        verify(mListenerMock1).onContactsFetchedFailure();
        verify(mListenerMock2).onContactsFetchedFailure();

    }

    // network error - observers notified of failure
    @Test
    public void fetchContacts_networkError_notifiedFailure() throws Exception {
        //Arrange
        networkError();
        //Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        //Assert
        verify(mListenerMock1).onContactsFetchedFailure();
        verify(mListenerMock2).onContactsFetchedFailure();

    }



    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactsScheme());
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private List<ContactSchema> getContactsScheme() {
        List<ContactSchema> contactSchemas = new ArrayList<>();
        contactSchemas.add(new ContactSchema(ID, FULL_NAME, FULL_PHONE_NUMBER,
                IMAGE_URL, AGE));
        return contactSchemas;
    }
}