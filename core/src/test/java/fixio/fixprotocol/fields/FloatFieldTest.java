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
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class FloatFieldTest {
	
	protected static final Charset US_ASCII = Charset.forName("US-ASCII");
	
    private FixedPointNumber value;
    private int tag;
    private FloatField field;

    @Before
    public void setUp() throws Exception {
        value = new FixedPointNumber(new Random().nextDouble(), 13);
        tag = new Random().nextInt();

        field = new FloatField(tag, value);
    }

    @Test
    public void testGetBytes() throws Exception {
        byte[] bytes = field.getBytes();

        byte[] expectedBytes = value.toString().getBytes(US_ASCII);

        assertArrayEquals(expectedBytes, bytes);
    }
}
