package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.UseCaseResult;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTest {
    public static final String USER_ID = "user_id";
    public static final String FULL_NAME = "full_name";
    public static final String IMAGE_URL = "image_url";

    FetchUserProfileUseCaseSync SUT;

    private UserProfileHttpEndpointSyncTd mUserProfileHttpEndpointSyncTd;
    private UsersCacheTd mUsersCacheTd;

    @Before
    public void setUp() throws Exception {
        mUserProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        mUsersCacheTd = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(mUserProfileHttpEndpointSyncTd, mUsersCacheTd);
    }

    @Test
    //fetchUserProfile - success - userFetchedFromEndpoint
    public void fetchUserProfileSync_success_userFetchedFromEndpoint() {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUserProfileHttpEndpointSyncTd.mUserId, is(USER_ID));

    }

    @Test
    //fetchUserProfile - success - userFetchedFromEndpoint
    public void fetchUserProfileSync_success_userCached() {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCacheTd.getUser(USER_ID).getUserId(), is(USER_ID));
        assertThat(mUsersCacheTd.getUser(USER_ID).getFullName(), is(FULL_NAME));
        assertThat(mUsersCacheTd.getUser(USER_ID).getImageUrl(), is(IMAGE_URL));

    }

    @Test
    public void fetchUserProfileSync_generalError_userNotCached() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void fetchUserProfileSync_serverError_userNotCached() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCacheTd.getUser(USER_ID), is(nullValue()));

    }

    @Test
    public void fetchUserProfileSync_networkError_userNotCached() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsNetworkError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCacheTd.getUser(USER_ID), is(nullValue()));

    }

    @Test
    public void fetchUserProfileSync_authError_userNotCached() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUsersCacheTd.getUser(USER_ID), is(nullValue()));

    }

    @Test
    public void fetchUserProfileSync_success_successReturned() throws Exception {
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void fetchUserProfileSync_serverError_failureReturned() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsServerError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }


    @Test
    public void fetchUserProfileSync_generalError_failureReturned() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsGeneralError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_authError_failureReturned() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsAuthError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_networkError_networkErrorReturned() throws Exception {
        mUserProfileHttpEndpointSyncTd.mIsNetworkError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }


    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {
        public boolean mIsGeneralError;
        public boolean mIsServerError;
        public boolean mIsAuthError;
        public boolean mIsNetworkError;
        String mUserId = "";

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;
            if (mIsGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (mIsServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            } else if (mIsAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            } else if (mIsNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, FULL_NAME, IMAGE_URL);
            }
        }
    }

    private static class UsersCacheTd implements UsersCache {
        List<User> mListUser = new ArrayList<>(1);

        @Override
        public void cacheUser(User user) {
            User cacheUser = getUser(user.getUserId());

            if (cacheUser != null) {
                mListUser.remove(0);
            }

            mListUser.add(user);

        }

        @Nullable
        @Override
        public User getUser(String userId) {
            for (User user : mListUser) {
                if (user.getUserId().equals(userId)) {
                    return user;
                }
            }

            return null;
        }
    }
}