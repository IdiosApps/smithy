/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.model.shapes;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import software.amazon.smithy.utils.BuilderRef;

/**
 * Abstract class representing service and resource shapes.
 */
public abstract class EntityShape extends Shape {

    private final Set<ShapeId> resources;
    private final Set<ShapeId> operations;

    EntityShape(Builder<?, ?> builder) {
        super(builder, false);

        if (getMixins().isEmpty()) {
            resources = builder.resources.copy();
            operations = builder.operations.copy();
        } else {
            Set<ShapeId> computedResources = new TreeSet<>();
            Set<ShapeId> computedOperations = new TreeSet<>();

            for (Shape shape : builder.getMixins().values()) {
                // validateMixins should have already assured that this is an EntityShape.
                EntityShape mixin = (EntityShape) shape;
                computedResources.addAll(mixin.getResources());
                computedOperations.addAll(mixin.getOperations());
            }

            computedResources.addAll(builder.resources.peek());
            computedOperations.addAll(builder.operations.peek());

            resources = Collections.unmodifiableSet(computedResources);
            operations = Collections.unmodifiableSet(computedOperations);
        }
    }

    /**
     * @return Get all of the resources directly bound to this shape.
     */
    public final Set<ShapeId> getResources() {
        return resources;
    }

    /**
     * Gets operations bound only through the "operations" property.
     *
     * <p>This will not include operations bound to resources using
     * a lifecycle operation binding. This will also not include
     * operations bound to this entity through sub-resources.
     *
     * @return Get the "operations" directly bound to this shape.
     * @see #getAllOperations()
     * @see software.amazon.smithy.model.knowledge.TopDownIndex#getContainedOperations
     */
    public final Set<ShapeId> getOperations() {
        return operations;
    }

    /**
     * Get all operations directly bound to this shape.
     *
     * <p>This will include operations bound directly to resources
     * using a lifecycle operation binding. This will not include
     * operations bound to this entity through sub-resources.
     *
     * @return Returns all operations bound to the shape.
     * @see software.amazon.smithy.model.knowledge.TopDownIndex#getContainedOperations
     */
    public Set<ShapeId> getAllOperations() {
        return getOperations();
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }

        EntityShape o = (EntityShape) other;
        return getResources().equals(o.getResources()) && getAllOperations().equals(o.getAllOperations());
    }

    /**
     * Builder used to create a Service or Resource shape.
     * @param <B> Concrete builder type.
     * @param <S> Shape type being created.
     */
    public abstract static class Builder<B extends Builder<B, S>, S extends EntityShape>
            extends AbstractShapeBuilder<B, S> {

        private final BuilderRef<Set<ShapeId>> resources = BuilderRef.forOrderedSet();
        private final BuilderRef<Set<ShapeId>> operations = BuilderRef.forOrderedSet();

        @SuppressWarnings("unchecked")
        public B operations(Collection<ShapeId> ids) {
            clearOperations();
            operations.get().addAll(ids);
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B addOperation(ToShapeId id) {
            operations.get().add(id.toShapeId());
            return (B) this;
        }

        public B addOperation(String id) {
            return addOperation(ShapeId.from(id));
        }

        @SuppressWarnings("unchecked")
        public B removeOperation(ToShapeId id) {
            operations.get().remove(id.toShapeId());
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B clearOperations() {
            operations.clear();
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B resources(Collection<ShapeId> ids) {
            clearResources();
            resources.get().addAll(ids);
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B addResource(ToShapeId id) {
            resources.get().add(id.toShapeId());
            return (B) this;
        }

        public B addResource(String id) {
            return addResource(ShapeId.from(id));
        }

        @SuppressWarnings("unchecked")
        public B removeResource(ToShapeId id) {
            resources.get().remove(id.toShapeId());
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B clearResources() {
            resources.clear();
            return (B) this;
        }

        @Override
        public B flattenMixins() {
            Set<ShapeId> flatResources = new TreeSet<>();
            Set<ShapeId> flatOperations = new TreeSet<>();

            for (Shape shape : getMixins().values()) {
                EntityShape mixin = (EntityShape) shape;
                flatResources.addAll(mixin.getResources());
                flatOperations.addAll(mixin.getOperations());
            }

            flatResources.addAll(resources.peek());
            flatOperations.addAll(operations.peek());
            resources(flatResources);
            operations(flatOperations);

            return super.flattenMixins();
        }
    }
}
