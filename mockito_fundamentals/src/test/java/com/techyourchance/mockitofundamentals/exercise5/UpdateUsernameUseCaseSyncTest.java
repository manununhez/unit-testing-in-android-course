package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UpdateUsernameUseCaseSyncTest {

    private static final String USER_ID = "userId";
    private static final String USERNAME = "userName";
    UpdateUsernameUseCaseSync SUT;
    private EventBusPoster mEvenBusPoster;
    private UsersCache mUsersCache;
    private UpdateUsernameHttpEndpointSync mUpdateUserNameHttpEndpointSync;

    @Before
    public void setUp() throws Exception {
        mUpdateUserNameHttpEndpointSync = mock(UpdateUsernameHttpEndpointSync.class);
        mUsersCache = mock(UsersCache.class);
        mEvenBusPoster = mock(EventBusPoster.class);

        SUT = new UpdateUsernameUseCaseSync(mUpdateUserNameHttpEndpointSync, mUsersCache, mEvenBusPoster);

        success();

    }

    @Test
    public void updateUsernameUseCaseSync_success_userIdAndNamePassedToEndpoint() throws Exception {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mUpdateUserNameHttpEndpointSync, times(1)).updateUsername(ac.capture(), ac.capture());
        List<String> captures = ac.getAllValues();
        assertThat(captures.get(0), is(USER_ID));
        assertThat(captures.get(1), is(USERNAME));
    }


    @Test
    public void updateUsernameUseCaseSync_success_usernameCached() throws Exception {
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mUsersCache).cacheUser(ac.capture());
        User cachedUser = ac.getValue();
        Assert.assertThat(cachedUser.getUserId(), is(USER_ID));
        Assert.assertThat(cachedUser.getUsername(), is(USERNAME));
    }

    @Test
    public void updateUsernameUseCaseSync_generalError_usernameNotCached() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mUsersCache);
    }

    @Test
    public void updateUsernameUseCaseSync_serverError_usernameNotCached() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mUsersCache);
    }

    @Test
    public void updateUsernameUseCaseSync_authError_usernameNotCached() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mUsersCache);
    }

    @Test
    public void updateUsernameUseCaseSync_success_usernameUpdatedEventPosted() throws Exception {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mEvenBusPoster).postEvent(ac.capture());
        Assert.assertThat(ac.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void updateUsernameUseCaseSync_generalError_noInteractionWithEventBusPoster() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mEvenBusPoster);
    }

    @Test
    public void updateUsernameUseCaseSync_serverError_noInteractionWithEventBusPoster() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mEvenBusPoster);
    }

    @Test
    public void updateUsernameUseCaseSync_authError_noInteractionWithEventBusPoster() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mEvenBusPoster);
    }

    @Test
    public void updateUsernameUseCaseSync_success_successReturned() throws Exception {
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));
    }


    @Test
    public void updateUsernameUseCaseSync_serverError_failureReturned() throws Exception {
        serverError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameUseCaseSync_authError_failureReturned() throws Exception {
        authError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameUseCaseSync_generalError_failureReturned() throws Exception {
        generalError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameUseCaseSync_networkError_networkErrorReturned() throws Exception {
        networkError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }


    private void generalError() throws Exception {
        when(mUpdateUserNameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void serverError() throws Exception {
        when(mUpdateUserNameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void authError() throws Exception {
        when(mUpdateUserNameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void success() throws Exception {
        when(mUpdateUserNameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void networkError() throws Exception {
        doThrow(new NetworkErrorException())
                .when(mUpdateUserNameHttpEndpointSync).updateUsername(any(String.class), any(String.class));
    }

}