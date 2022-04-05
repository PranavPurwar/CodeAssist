package com.tyron.builder.api.internal.reflect.annotations;


import com.tyron.builder.api.internal.reflect.AnnotationCategory;
import com.tyron.builder.api.internal.reflect.validation.TypeValidationContext;

/**
 * A validating metadata store that handles annotations on types and their JavaBeans properties.
 *
 * <p>
 * The store considers property annotations to belong to {@linkplain AnnotationCategory categories}.
 * Each property can have at most one annotation per category.
 * Validation failures with a given type can be visited using {@link TypeAnnotationMetadata#visitValidationFailures(TypeValidationContext)}.
 * </p>
 *
 * <strong>Property annotation inheritance</strong>
 *
 * <p>
 * Property annotations are inherited from super-types on a per-category basis: subtypes can override each super-type annotation category separately.
 * If a subtype doesn't override a super-type defined category, the corresponding annotation in that category gets inherited.
 * Subtypes can mark methods to be ignored by using an ignore annotation.
 * Ignored methods don't inherit super-type annotations.
 * </p>
 *
 * <strong>Boolean properties</strong>
 *
 * <p>
 * In JavaBeans, {@code boolean} properties are allowed to be accessed both via {@code is}-getters and {@code get}-getters.
 * However, when both getter methods are present, Gradle needs to decide which method to use to access the property value. This can be solved
 * by explicitly ignoring one of the two getters using the ignore annotation.
 * </p>
 *
 * <p>
 * A special case of this is when both getters are generated by Groovy.
 * The store implementation can be supplied with a {@link java.util.function.Predicate} to identify generated getters.
 * If the {@code is}-getter is generated, and the {@code get}-getter is not ignored explicitly, then the generated
 * {@code is}-getter is automatically ignored in favor of the {@code get}-getter.
 * </p>
 */
public interface TypeAnnotationMetadataStore {
    TypeAnnotationMetadata getTypeAnnotationMetadata(Class<?> type);
}