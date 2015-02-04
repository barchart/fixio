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
package fixio.fixprotocol;

import fixio.fixprotocol.fields.CharField;
import fixio.fixprotocol.fields.FixedPointNumber;
import fixio.fixprotocol.fields.StringField;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents FIX Protocol field Group or Component - a sequence of Fields or other Groups.
 */
public class Group implements FieldListBuilder<Group> {

    private static final int DEFAULT_GROUP_SIZE = 8;
    private final ArrayList<FixMessageFragment> contents;

    public Group(int expectedSize) {
        this.contents = new ArrayList<FixMessageFragment>(expectedSize);
    }

    public Group() {
        this.contents = new ArrayList<FixMessageFragment>(DEFAULT_GROUP_SIZE);
    }

    public void add(FixMessageFragment element) {
        contents.add(element);
    }
    
    @Override
	public Group add(FieldType field, char value) {
    	contents.add(new CharField(field.tag(), value));
		return this;
	}

    @Override
    public Group add(FieldType fieldType, String value) {
        contents.add(new StringField(fieldType.tag(), value));
        return this;
    }

    @Override
    public Group add(int tagNum, String value) {
        FieldListBuilderHelper.add(contents, tagNum, value);
        return this;
    }

    @Override
    public Group add(DataType type, int tagNum, String value) {
        FieldListBuilderHelper.add(contents, type, tagNum, value);
        return this;
    }

    @Override
    public Group add(FieldType field, int value) {
        FieldListBuilderHelper.add(contents, field, value);
        return this;
    }

    @Override
    public Group add(int tagNum, int value) {
        FieldListBuilderHelper.add(contents, tagNum, value);
        return this;
    }

    @Override
    public Group add(DataType type, int tagNum, int value) {
        FieldListBuilderHelper.add(contents, tagNum, value);
        return this;
    }

    @Override
    public Group add(FieldType field, long value) {
        FieldListBuilderHelper.add(contents, field, value);
        return this;
    }

    @Override
    public Group add(int tagNum, long value) {
        FieldListBuilderHelper.add(contents, tagNum, value);
        return this;
    }

    @Override
    public Group add(DataType type, int tagNum, long value) {
        FieldListBuilderHelper.add(contents, type, tagNum, value);
        return this;
    }

    @Override
    public Group add(FieldType fieldType, FixedPointNumber value) {
        FieldListBuilderHelper.add(contents, fieldType, value);
        return this;
    }

    @Override
    public Group add(int tagNum, FixedPointNumber value) {
        FieldListBuilderHelper.add(contents, tagNum, value);
        return this;
    }

    @Override
    public Group add(DataType type, int tagNum, FixedPointNumber value) {
        FieldListBuilderHelper.add(contents, type, tagNum, value);
        return this;
    }

    public List<FixMessageFragment> getContents() {
        return contents;
    }

}
