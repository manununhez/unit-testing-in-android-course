package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

public class FetchReputationUseCaseSync {

    private final GetReputationHttpEndpointSync mGetReputationHttpEndpointSync;

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync mGetReputationHttpEndpointSync) {
        this.mGetReputationHttpEndpointSync = mGetReputationHttpEndpointSync;
    }

    public UseCaseResult getReputationSync() {
        GetReputationHttpEndpointSync.EndpointResult result = mGetReputationHttpEndpointSync.getReputationSync();

        switch (result.getStatus()) {
            case GENERAL_ERROR:
            case NETWORK_ERROR:
                return new UseCaseResult(Status.FAILURE, result.getReputation());
            case SUCCESS:
                return new UseCaseResult(Status.SUCCESS, result.getReputation());
            default:
                throw new RuntimeException("Invalid result status:" + result.getStatus());

        }

    }

    enum Status {
        FAILURE, SUCCESS
    }

    static class UseCaseResult {
        private final Status mStatus;

        private final int mReputation;

        public UseCaseResult(Status status, int reputation) {
            mStatus = status;
            mReputation = reputation;
        }

        public Status getStatus() {
            return mStatus;
        }

        public int getReputation() {
            return mReputation;
        }
    }
}
