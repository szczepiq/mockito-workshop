package org.workshop;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * By Szczepan Faber on 6/22/12
 */
@RunWith(MockitoJUnitRunner.class)
public class SafeHistoryTest {

    @Mock History delegate;
    @InjectMocks SafeHistory history;

    @Test
    public void shouldDelegateHistoryUpdates() throws Exception {
        //given
        TranslationRequest request = new TranslationRequest("foo");
        TranslationResult result = new TranslationResult("bar");

        //when
        history.lookUpAttempted(request);
        history.lookUpCompleted(result);

        //then
        verify(delegate).lookUpAttempted(request);
        verify(delegate).lookUpCompleted(result);
    }

    @Test
    public void shouldBeLenientForFailedAttempts() throws Exception {
        //given
        doThrow(new HistoryUpdateFailure())
                .when(delegate).lookUpAttempted(any(TranslationRequest.class));

        //when
        history.lookUpAttempted(new TranslationRequest("foo"));

        //then no exception is thrown
    }

    @Test
    public void shouldBeLenientIfHistoryFailsAtTranslationCompleted() throws Exception {
        //given
        doThrow(new HistoryUpdateFailure())
                .when(delegate).lookUpCompleted(any(TranslationResult.class));

        //when
        history.lookUpCompleted(new TranslationResult("bar"));

        //then no exception is thrown
    }
}
