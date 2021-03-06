package org.workshop;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.workshop.TestSupport.translationRequestFor;

/**
 * By Szczepan Faber on 6/21/12
 */
@RunWith(MockitoJUnitRunner.class)
public class SmartDictionaryTest {

    @Mock
    Translator translator;
    @Mock
    History history;
    @InjectMocks
    SmartDictionary dictionary;

    @Before public void before() {
        when(translator.translate(any(TranslationRequest.class)))
                .thenReturn(new TranslationResult("foo"));
    }

    @Test
    public void shouldLookUpWords() throws Exception {
        //given
        when(translator.translate(translationRequestFor("mockito")))
                .thenReturn(new TranslationResult("cool stuff"));

        //when
        String result = dictionary.lookUp("mockito");

        //then
        assertEquals("cool stuff", result);
    }

    @Test
    public void shouldKeepHistoryOfRequests() throws Exception {
        //when
        dictionary.lookUp("uberconf");

        //then
        verify(history).lookUpAttempted(translationRequestFor("uberconf"));
    }

    @Test
    public void shouldKeepHistoryOfResults() throws Exception {
        TranslationResult result = new TranslationResult("nice place");
        when(translator.translate(translationRequestFor("Denver")))
                .thenReturn(result);

        //when
        dictionary.lookUp("Denver");

        //then
        verify(history).lookUpCompleted(result);
    }

    @Test
    public void shouldLookUpRequestsBeRememberedBeforeResults() throws Exception {
        //when
        dictionary.lookUp("Westin");

        //then
        InOrder inOrder = Mockito.inOrder(history);
        inOrder.verify(history)
                .lookUpAttempted(any(TranslationRequest.class));
        inOrder.verify(history)
                .lookUpCompleted(any(TranslationResult.class));
    }

    @Test
    public void shouldThrowMeaningfulExceptionWhenTranslationFails() throws Exception {
        //given
        when(translator.translate(any(TranslationRequest.class)))
                .thenThrow(new TranslationFailed());

        //when
        try {
            dictionary.lookUp("Colorado");
            //then
            fail("Expected exception");
        } catch (LookUpFailed e) {}
    }
}
