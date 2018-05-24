package com.ehi.enterprise.mock.android;

import android.content.Context;
import android.content.res.Resources;

import com.ehi.enterprise.helpers.MockableObject;

import org.mockito.invocation.InvocationOnMock;

/**
 * A "default" for mocking context in POJO tests. Utilize this to return "stubs" such as empty strings to a viewmodel which calles getResources().getString() etc.
 * You can utilize {@link #getMockedResources()} to stub out more custom behaviour using the {@link MockableObject} object to add Asserts or various answers including overriding the current default one.
 */
public class DefaultMockedContext {

    private final MockableObject<Resources> mMockedResources;
    private final MockableObject<Context> mMockedContext;

    public DefaultMockedContext(){
        mMockedResources = new MockableObject<>(Resources.class)
                                .addDefaultAnswer(new MockableObject.TestAnswer() {
                                    @Override
                                    public Object provideAnswer(InvocationOnMock invocation) {
                                        if(invocation.getMethod().getName().equals("getString")){
                                            return "";
                                        }
                                        return null;
                                    }
                                });

        mMockedContext = new MockableObject<>(Context.class)
                        .addAnswer(new MockableObject.TestAnswer() {
                            @Override
                            public Object provideAnswer(InvocationOnMock invocation) {
                                if(invocation.getMethod().getName().equals("getResources")){
                                    return mMockedResources;
                                }
                                return null;
                            }
                        });
    }

    public MockableObject<Context> getMockedContext() {
        return mMockedContext;
    }

    public MockableObject<Resources> getMockedResources() {
        return mMockedResources;
    }
}
