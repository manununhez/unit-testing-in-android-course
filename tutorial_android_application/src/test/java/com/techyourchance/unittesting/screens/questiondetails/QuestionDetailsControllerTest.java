package com.techyourchance.unittesting.screens.questiondetails;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerTest {

    public static final QuestionDetails QUESTION_DETAILS = new QuestionDetails("id", "title", "body");
    public static final String QUESTION_ID = "id";
    QuestionDetailsController SUT;
    @Mock
    ScreensNavigator mScreensNavigatorMock;
    @Mock
    ToastsHelper mToastHelperMock;
    @Mock
    QuestionDetailsViewMvc mQuestionDetailsViewMvcMock;

    private FetchQuestionDetailsUseCaseTd mFetchQuestionDetailsUseCaseTd;

    @Before
    public void setUp() throws Exception {
        mFetchQuestionDetailsUseCaseTd = new FetchQuestionDetailsUseCaseTd();
        SUT = new QuestionDetailsController(mFetchQuestionDetailsUseCaseTd, mScreensNavigatorMock, mToastHelperMock);
        SUT.bindView(mQuestionDetailsViewMvcMock);
        SUT.bindQuestionId(QUESTION_ID);
    }


    @Test
    public void onStart_progressIndicationShown() throws Exception {
        //Arrange
        //Act
        SUT.onStart();
        //Assert
        verify(mQuestionDetailsViewMvcMock).showProgressIndication();
    }

    @Test
    public void onStart_successfulresponse_detailsBoundToView() throws Exception {
        //Arrange
        success();
        //Act
        SUT.onStart();
        //Assert
        verify(mQuestionDetailsViewMvcMock).bindQuestion(QUESTION_DETAILS);
    }

    @Test
    public void onStart_successfulresponse_progressIndicationHidden() throws Exception {
        //Arrange
        success();
        //Act
        SUT.onStart();
        //Assert
        verify(mQuestionDetailsViewMvcMock).hideProgressIndication();
    }


    //on failure  details fetched - error toast is shown
    @Test
    public void onStart_failure_errorToastShown() throws Exception {
        //Arrange
        failure();
        //Act
        SUT.onStart();
        //Assert
        verify(mToastHelperMock).showUseCaseError();
    }

    @Test
    public void onStart_failure_progressIndicationHidden() throws Exception {
        //Arrange
        failure();
        //Act
        SUT.onStart();
        //Assert
        verify(mQuestionDetailsViewMvcMock).hideProgressIndication();
    }


    @Test
    public void onStart_failure_detailsNotBoundToView() throws Exception {
        //Arrange
        failure();
        //Act
        SUT.onStart();
        //Assert
        verify(mQuestionDetailsViewMvcMock, never()).bindQuestion(any(QuestionDetails.class));
    }
    //onStart - register listeners

    @Test
    public void onStart_listenersRegistered() throws Exception {
        //Arrange
        //Act
        SUT.onStart();
        //Assert
        verify(mQuestionDetailsViewMvcMock).registerListener(SUT);
        mFetchQuestionDetailsUseCaseTd.verifyRegisteredListeners(SUT);
    }

    //onStop - unregister listeners
    @Test
    public void onStop_listenersUnregistered() throws Exception {
        //Arrange
        //Act
        SUT.onStop();
        //Assert
        verify(mQuestionDetailsViewMvcMock).unregisterListener(SUT);
        mFetchQuestionDetailsUseCaseTd.verifyNotRegisteredListeners(SUT);
    }
    //on navigate up clicked - navigate back to questions lists


    @Test
    public void onNavigateUpClicked_navigateBackToQuestionListScreen() throws Exception {
        //Arrange
        //Act
        SUT.onNavigateUpClicked();
        //Assert
        verify(mScreensNavigatorMock).navigateUp();
    }


    private void success() {
        //currently no-op
    }

    private void failure() {
        mFetchQuestionDetailsUseCaseTd.mFailure = true;
    }


    private static class FetchQuestionDetailsUseCaseTd extends FetchQuestionDetailsUseCase {

        public boolean mFailure;

        public FetchQuestionDetailsUseCaseTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetailsAndNotify(String questionId) {
            for (FetchQuestionDetailsUseCase.Listener listener : getListeners()) {
                if (mFailure) {
                    listener.onQuestionDetailsFetchFailed();

                } else {
                    listener.onQuestionDetailsFetched(QUESTION_DETAILS);

                }
            }
        }

        public void verifyRegisteredListeners(QuestionDetailsController candidate) {
            for (FetchQuestionDetailsUseCase.Listener listener : getListeners()) {
                if (listener == candidate)
                    return;
            }

            throw new RuntimeException("Listener not registered");
        }

        public void verifyNotRegisteredListeners(QuestionDetailsController candidate) {
            for (FetchQuestionDetailsUseCase.Listener listener : getListeners()) {
                if (listener == candidate)
                    throw new RuntimeException("Listener not registered");
            }

        }

    }
}