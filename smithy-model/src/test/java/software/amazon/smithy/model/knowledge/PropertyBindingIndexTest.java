/*
 * Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *   http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.model.knowledge;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.ShapeId;

public class PropertyBindingIndexTest {

    @Test
    public void testIndex() {
        Model model = Model.assembler()
                .addImport(OperationIndexTest.class.getResource("member-property-index.smithy"))
                .assemble()
                .unwrap();

        PropertyBindingIndex index = PropertyBindingIndex.of(model);
        assertFalse(index.doesMemberShapeRequireProperty(ShapeId.from("com.example#ResourceStructure_1$token")));
        assertFalse(index.doesMemberShapeRequireProperty(ShapeId.from("com.example#ResourceStructure_1$id")));
        assertFalse(index.doesMemberShapeRequireProperty(ShapeId.from("com.example#ResourceStructure_1$spurious")));
        assertFalse(index.doesMemberShapeRequireProperty(ShapeId.from("com.example#ResourceStructure_2$nested")));
        assertTrue(index.doesMemberShapeRequireProperty(ShapeId.from("com.example#ResourceStructure_1$property")));
    }
}
