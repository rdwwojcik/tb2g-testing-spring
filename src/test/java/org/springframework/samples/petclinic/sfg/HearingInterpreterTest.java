package org.springframework.samples.petclinic.sfg;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HearingInterpreterTest {

    HearingInterpreter hearingInterpreter;

    @Before
    public void setUp() throws Exception {
        hearingInterpreter = new HearingInterpreter(new LaurelWordProducer());
    }

    @Test
    public void whatIheard() {
        String word = hearingInterpreter.whatIheard();

        assertEquals(word, "Laurel");
    }
}