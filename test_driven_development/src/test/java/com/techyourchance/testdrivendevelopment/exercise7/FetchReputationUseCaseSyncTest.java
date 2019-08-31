package com.techyourchance.testdrivendevelopment.exercise7;


import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.Status.FAILURE;
import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.Status.SUCCESS;
import static com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.UseCaseResult;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {

    private static final int REPUTATION = 5;
    private static final int REPUTATION_ZERO = 0;
    @Mock
    GetReputationHttpEndpointSync mGetReputationHttpEndpointSyncMock;
    private FetchReputationUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchReputationUseCaseSync(mGetReputationHttpEndpointSyncMock);

        success();
    }


    @Test
    public void fetchReputationSync_success_successReturned() throws Exception {
        //Arrange
        //Act
        UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getStatus(), is(SUCCESS));
    }

    @Test
    public void fetchReputationSync_success_reputationReturned() throws Exception {
        //Arrange
        //Act
        UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getReputation(), is(REPUTATION));
    }

    @Test
    public void fetchReputationSync_generalError_failureReturned() throws Exception {
        //Arrange
        generalError();
        //Act
        UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getStatus(), is(FAILURE));
    }

    @Test
    public void fetchReputationSync_generalError_reputationZeroReturned() throws Exception {
        //Arrange
        generalError();
        //Act
        UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getReputation(), is(REPUTATION_ZERO));
    }

    @Test
    public void fetchReputationSync_networkError_failureReturned() throws Exception {
        //Arrange
        networkError();
        //Act
        UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getStatus(), is(FAILURE));
    }

    @Test
    public void fetchReputationSync_networkError_reputationZeroReturned() throws Exception {
        //Arrange
        networkError();
        //Act
        UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getReputation(), is(REPUTATION_ZERO));
    }


    private void networkError() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(
                new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR, REPUTATION_ZERO));
    }

    private void generalError() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(
                new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR, REPUTATION_ZERO));
    }

    private void success() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(
                new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.SUCCESS, REPUTATION));
    }
}