package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;


public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    private FetchUserHttpEndpointSync mFetchUserHttpEndpointSync;
    private UsersCache mUsersCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync mFetchUserHttpEndpointSync,
                                    UsersCache mUsersCache) {

        this.mFetchUserHttpEndpointSync = mFetchUserHttpEndpointSync;
        this.mUsersCache = mUsersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        FetchUserHttpEndpointSync.EndpointResult result;
        try {
            if (mUsersCache.getUser(userId) == null) {
                result = mFetchUserHttpEndpointSync.fetchUserSync(userId);
            } else {
                return new UseCaseResult(Status.SUCCESS, mUsersCache.getUser(userId));
            }
        } catch (NetworkErrorException e) {
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }

        switch (result.getStatus()) {
            case SUCCESS:
                User userToCached = new User(result.getUserId(), result.getUsername());
                mUsersCache.cacheUser(userToCached);
                return new UseCaseResult(Status.SUCCESS, userToCached);
            case AUTH_ERROR:
            case GENERAL_ERROR:
                return new UseCaseResult(Status.FAILURE, null);
            default:
                throw new RuntimeException("invalid endpoint result: " + result);
        }
    }

}
