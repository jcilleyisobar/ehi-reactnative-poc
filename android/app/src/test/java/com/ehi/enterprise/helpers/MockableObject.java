package com.ehi.enterprise.helpers;

import com.ehi.enterprise.android.ui.navigation.NavDrawerViewModelTest;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use this class whenever standard mockito behaviour is not sufficient. To see ways to utilize this properly look at {@link com.ehi.enterprise.mock.android.DefaultMockedContext}
 *
 * This class allows the addition of return values and asserts in an as-needed bases. Additionally, it can be used to create a utility class which implements an
 * {@link MockableObject.TestAnswer} or {@link com.ehi.enterprise.helpers.MockableObject.MockableObjectCallback} in order to change its behaviour.
 * For an example of a simple way to follow this pattern see {@link NavDrawerViewModelTest.LoginAnswer}
 * @param <T> The mocked object class
 *
 * @see com.ehi.enterprise.helpers.MockableObject.TestAnswer
 * @see com.ehi.enterprise.helpers.MockableObject.MockableObjectCallback
 * @see NavDrawerViewModelTest.LoginAnswer
 */
public class MockableObject<T> {

    private final T mMockedObject;
    private List<TestAnswer> mAnswerCallbacks;
    private List<MockableObjectCallback> mMockableObjectCallbacks;
    private TestAnswer mDefaultAnswer;
    private Map<String, Object> mShortHandAnswers;

    public MockableObject(Class<T> klass){

        mAnswerCallbacks = new ArrayList<>(1);
        mMockableObjectCallbacks = new ArrayList<>(1);
        mShortHandAnswers = new HashMap<>(1);

        mMockedObject = Mockito.mock(klass, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                for(int i=0; i < mMockableObjectCallbacks.size(); i++){
                    mMockableObjectCallbacks.get(i).callback(invocation);
                }

                Object result = mShortHandAnswers.get(invocation.getMethod().getName());
                if(result != null){
                    return result;
                }

                for(int i=0; i < mAnswerCallbacks.size(); i++){
                    result = mAnswerCallbacks.get(i).provideAnswer(invocation);
                    if(result != null){
                        return result;
                    }
                }
                if(mDefaultAnswer != null){
                    return mDefaultAnswer.provideAnswer(invocation);
                }
                return null;
            }
        });
    }

    public T getMockedObject() {
        return mMockedObject;
    }

    /**
     * Will add {@link com.ehi.enterprise.helpers.MockableObject.TestAnswer} that this mockable object will return based on a certain method call.
     * Return Null if you do not have an answer to return. However if you do want to return null in an answer to a method call,
     * either use Mockito for your edge case or utilize {@link #addAnswer(String, Object)} as it does not check for return value before passing it on to the calling object.
     * @param answer
     * @return
     */
    public MockableObject<T> addAnswer(TestAnswer answer){
        mAnswerCallbacks.add(answer);
        return this;
    }

    /**
     * A short hand way of adding "answers" keep in mind, if you add a short-hand answer, it WILL return what ever the result object you pass in is. This is because there is no
     * null check. If you require a more precise implementation based upon parameters etc, utilize {@link #addAnswer(TestAnswer)}
     * @param methodCall Name of method which you wish to intercept (No parenthesis required)
     * @param result The answer you wish to return
     * @return
     */
    public MockableObject<T> addAnswer(String methodCall, Object result){
        mShortHandAnswers.put(methodCall, result);
        return this;
    }

    /**
     * If no answer is found from either {@link #addAnswer(TestAnswer)} or {@link #addAnswer(String, Object)} then this default answer will be the final return value.
     * @param answer
     * @return
     */
    public MockableObject<T> addDefaultAnswer(TestAnswer answer){
        mDefaultAnswer = answer;
        return this;
    }

    /**
     * Callbacks are best utilized for asserts or to simply examine the parameters of method calls. A sample usage can be found in {@link NavDrawerViewModelTest#testCorrectDrawerItems()}
     * @param callback The {@link MockableObjectCallback} that is guaranteed to be called each time the mock is used
     * @return
     */
    public MockableObject<T> addCallback(MockableObjectCallback callback){
        mMockableObjectCallbacks.add(callback);
        return this;
    }

    public interface TestAnswer{
        /**
         * A callback for an answer which you wish to provide based upon the invocation below. Use this when you have a specific scenario in mind.
         * @see org.mockito.invocation.InvocationOnMock
         * @param invocation Use this to find out the method name, its parameters.
         * @return Desired return value based on parameter invocation return null to not consume the method call
         */
        Object provideAnswer(InvocationOnMock invocation);
    }


    public interface MockableObjectCallback {
        /**
         * Useful for asserts/examining what a method is being passed.
         * @param invocationOnMock
         */
        void callback(InvocationOnMock invocationOnMock);
    }
}
