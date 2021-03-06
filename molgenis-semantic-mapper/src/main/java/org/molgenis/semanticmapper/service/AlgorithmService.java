package org.molgenis.semanticmapper.service;

import java.util.Collection;
import java.util.List;
import org.molgenis.data.Entity;
import org.molgenis.data.meta.model.Attribute;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.semanticmapper.mapping.model.AttributeMapping;
import org.molgenis.semanticmapper.mapping.model.EntityMapping;
import org.molgenis.semanticmapper.service.impl.AlgorithmEvaluation;

public interface AlgorithmService {
  /**
   * Applies an algorithm to the given attribute of given source entities.
   *
   * @return algorithm evaluation for each source entity
   */
  Iterable<AlgorithmEvaluation> applyAlgorithm(
      Attribute targetAttribute, String algorithm, Iterable<Entity> sourceEntities);

  /**
   * Bind the current context to a source entity
   *
   * @param sourceEntity the entity to bind to
   */
  void bind(Entity sourceEntity);

  /**
   * Applies an {@link AttributeMapping} to the source {@link Entity} currently bound to the
   * context.
   *
   * @param attributeMapping {@link AttributeMapping} to apply
   * @return Object containing the mapped value
   */
  Object apply(AttributeMapping attributeMapping);

  /**
   * Retrieves the names of the source attributes in an algorithm
   *
   * @param algorithmScript String with the algorithm script
   * @return Collection of source attribute name Strings
   */
  Collection<String> getSourceAttributeNames(String algorithmScript);

  /** Creates an attribute mapping after the semantic search service finds one */
  void autoGenerateAlgorithm(
      EntityType sourceEntityType, EntityType targetEntityType, EntityMapping mapping);

  /**
   * Generates the algorithm based on the given targetAttribute and sourceAttribute
   *
   * @return the generate algorithm
   */
  String generateAlgorithm(
      Attribute targetAttribute,
      EntityType targetEntityType,
      List<Attribute> sourceAttributes,
      EntityType sourceEntityType);

  /**
   * Copies all algorithms from a source entity type to a target entity type. Sets the algorithm
   * state of each attribute mapping copy to 'discuss'.
   */
  void copyAlgorithms(EntityMapping sourceEntityMapping, EntityMapping targetEntityMapping);
}
