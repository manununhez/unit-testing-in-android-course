package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {

    public static final String QUESTION_ID = "id";
    FetchQuestionDetailsUseCase SUT;
    @Mock
    FetchQuestionDetailsUseCase.Listener mListener1;
    @Mock
    FetchQuestionDetailsUseCase.Listener mListener2;

    @Captor
    ArgumentCaptor<List<QuestionDetails>> mAcQuestionDetails;

    private EndpointTd mEndpointTd;

    @Before
    public void setUp() throws Exception {
        mEndpointTd = new EndpointTd();
        SUT = new FetchQuestionDetailsUseCase(mEndpointTd);
    }

    private void success() {
        //currently no-op

    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_listenerNotifiedWithCorrectData() throws Exception {
        //Arrange
        success();
        ArgumentCaptor<QuestionDetails> ac = ArgumentCaptor.forClass(QuestionDetails.class);
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        //Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        //Assert
        verify(mListener1).onQuestionDetailsFetched(ac.capture());
        verify(mListener2).onQuestionDetailsFetched(ac.capture());
        List<QuestionDetails> allValues = ac.getAllValues();
        assertThat(allValues.get(0).getId(), is(QUESTION_ID));
        assertThat(allValues.get(1).getId(), is(QUESTION_ID));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenerNotifiedWithFailure() throws Exception {
        //Arrange
        failure();
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        //Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        //Assert
        verify(mListener1).onQuestionDetailsFetchFailed();
        verify(mListener2).onQuestionDetailsFetchFailed();
    }

    private void failure() {
        mEndpointTd.mFailure = true;
    }

    private QuestionDetails getQuestionDetails() {
        return null;
    }

    private static class EndpointTd extends FetchQuestionDetailsEndpoint {

        public boolean mFailure;

        public EndpointTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(String questionId, Listener listener) {
            if (mFailure) {
                listener.onQuestionDetailsFetchFailed();
            } else {
                listener.onQuestionDetailsFetched(new QuestionSchema("title", "id", "body"));
            }
        }
    }

}