/*
 * Copyright 2014 The FIX.io Project
 *
 * The FIX.io Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package fixio.fixprotocol.fields;

import static org.junit.Assert.assertArrayEquals;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Random;

import org.junit.Test;

public class IntFieldTest {

	protected static final Charset US_ASCII = Charset.forName("US_ASCII");
	
    @Test
    public void testGetBytes() throws Exception {
        int value = new Random().nextInt();
        int tag = new Random().nextInt();

        IntField field = new IntField(tag, value);
        byte[] bytes = field.getBytes();

        assertArrayEquals(String.valueOf(value).getBytes(US_ASCII), bytes);
    }

    @Test(expected = ParseException.class)
    public void testFailParseNonInteger() throws ParseException {
        new IntField(10, "12a345".getBytes(US_ASCII), 0, 6);
    }
}
