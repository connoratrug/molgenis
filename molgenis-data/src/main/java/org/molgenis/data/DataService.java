package org.molgenis.data;

import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.molgenis.data.aggregation.AggregateQuery;
import org.molgenis.data.aggregation.AggregateResult;
import org.molgenis.data.meta.MetaDataService;
import org.molgenis.data.meta.model.EntityType;

/**
 * DataService is a facade that manages data sources Entity names should be unique over all data
 * sources.
 *
 * <p>Main entry point for the DataApi
 */
public interface DataService extends Iterable<Repository<Entity>> {
  void setMetaDataService(MetaDataService metaDataService);

  /**
   * Get the MetaDataService
   *
   * @return meta data service
   */
  MetaDataService getMeta();

  /**
   * Get the capabilities of a repository
   *
   * @param repositoryName repository name
   * @return repository capabilities
   */
  Set<RepositoryCapability> getCapabilities(String repositoryName);

  /**
   * Returns whether a repository exists for the given entity type identifier.
   *
   * @param entityTypeId entity type identifier
   * @return true if a repository exists for the given entity type identifier
   */
  boolean hasRepository(String entityTypeId);

  /**
   * Returns the repository for the given entity type identifier.
   *
   * @param entityTypeId entity type identifier
   * @return repository, never null
   * @throws UnknownEntityTypeException if no entity type with the given identifier exists
   * @throws UnknownRepositoryException if no repository exists for the entity type
   */
  Repository<Entity> getRepository(String entityTypeId);

  /**
   * Returns the typed repository for the given entity type identifier.
   *
   * @param entityTypeId entity type identifier
   * @param entityTypeClass entity type class
   * @return typed repository, never null
   * @throws UnknownEntityTypeException if no entity type with the given identifier exists
   * @throws UnknownRepositoryException if no repository exists for the entity type
   */
  <E extends Entity> Repository<E> getRepository(String entityTypeId, Class<E> entityTypeClass);

  /**
   * Returns whether an entity type exists for the given entity type identifier.
   *
   * @param entityTypeId entity type identifier
   * @return true if an entity type exists for the given entity type identifier
   */
  boolean hasEntityType(String entityTypeId);

  /**
   * Returns the entity type for the given entity type identifier.
   *
   * @param entityTypeId entity type identifier
   * @return entity type, never null
   * @throws UnknownEntityTypeException if no entity type with the given identifier exists
   */
  EntityType getEntityType(String entityTypeId);

  /**
   * Returns the number of entities of the given type.
   *
   * @param entityTypeId entity name
   * @return number of entities
   */
  long count(String entityTypeId);

  /**
   * return number of entities matched by query
   *
   * @throws MolgenisDataException if the repository of the entity isn't a Queryable
   */
  long count(String entityTypeId, Query<Entity> q);

  /**
   * Find all entities of the given type. Returns empty Stream if no matches.
   *
   * @throws MolgenisDataException if the repository of the entity isn't a Queryable
   */
  Stream<Entity> findAll(String entityTypeId);

  /** type-safe find all entities */
  <E extends Entity> Stream<E> findAll(String entityTypeId, Class<E> clazz);

  /**
   * Find entities that match a query. Returns empty stream if no matches.
   *
   * <p>throws MolgenisDataException if the repository of the entity isn't a Queryable
   */
  Stream<Entity> findAll(String entityTypeId, Query<Entity> q);

  /**
   * Type-safe find entities that match a query
   *
   * @param q query
   * @param clazz entity class
   */
  <E extends Entity> Stream<E> findAll(String entityTypeId, Query<E> q, Class<E> clazz);

  /**
   * Finds all entities with the given IDs. Returns empty stream if no matches.
   *
   * @param ids entity ids
   * @return (empty) Stream where the order of entities matches the order of ids, never null
   */
  Stream<Entity> findAll(String entityTypeId, Stream<Object> ids);

  /**
   * Finds all entities with the given IDs, type-safely. Returns empty stream if no matches.
   *
   * @param entityTypeId entity name (case insensitive)
   * @return (empty) Stream where the order of entities matches the order of ids, never null
   */
  <E extends Entity> Stream<E> findAll(String entityTypeId, Stream<Object> ids, Class<E> clazz);

  /**
   * Finds all entities with the given IDs, with a fetch. Returns empty stream if no matches.
   *
   * @param entityTypeId entity name (case insensitive)
   * @param ids entity ids
   * @param fetch fetch defining which attributes to retrieve
   * @return (empty) Stream where the order of entities matches the order of ids, never null
   */
  Stream<Entity> findAll(String entityTypeId, Stream<Object> ids, Fetch fetch);

  /**
   * Finds all entities with the given IDs, type-safely and with a fetch. Returns empty stream if no
   * matches.
   *
   * @param ids entity ids
   * @param fetch fetch defining which attributes to retrieve
   * @param clazz typed entity class
   * @return (empty) Stream of entities of the give type where the order of entities matches the
   *     order of ids, never null
   */
  <E extends Entity> Stream<E> findAll(
      String entityTypeId, Stream<Object> ids, Fetch fetch, Class<E> clazz);

  /**
   * Find one entity based on id. Returns null if not exists
   *
   * <p>throws MolgenisDataException if the repository of the entity isn't a Queryable
   */
  @Nullable
  @CheckForNull
  Entity findOneById(String entityTypeId, Object id);

  /**
   * @param id entity id
   * @param clazz entity type
   * @return typed entity
   */
  @Nullable
  @CheckForNull
  <E extends Entity> E findOneById(String entityTypeId, Object id, Class<E> clazz);

  /**
   * Find one entity based on id.
   *
   * @param id entity id
   * @param fetch fetch defining which attributes to retrieve
   * @return entity or null
   */
  @Nullable
  @CheckForNull
  Entity findOneById(String entityTypeId, Object id, Fetch fetch);

  /**
   * Type-safe find one entity based on id.
   *
   * @param id entity id
   * @param fetch fetch defining which attributes to retrieve
   * @param clazz typed entity class
   * @return entity of the given type or null
   */
  @Nullable
  @CheckForNull
  <E extends Entity> E findOneById(String entityTypeId, Object id, Fetch fetch, Class<E> clazz);

  /**
   * Find one entity based on id. Returns null if not exists
   *
   * @throws MolgenisDataException if the repository of the entity isn't a Queryable
   */
  @Nullable
  @CheckForNull
  Entity findOne(String entityTypeId, Query<Entity> q);

  /**
   * type-save find an entity by it's id
   *
   * @param q query
   */
  @Nullable
  @CheckForNull
  <E extends Entity> E findOne(String entityTypeId, Query<E> q, Class<E> clazz);

  /**
   * Adds an entity to it's repository Note: the caller is responsible for updating the other side
   * of bidirectional relationships (e.g. one-to-many attributes).
   *
   * @throws MolgenisDataException if the repository of the entity isn't a Writable
   */
  void add(String entityTypeId, Entity entity);

  /**
   * Adds entities to it's repository Note: the caller is responsible for updating the other side of
   * bidirectional relationships (e.g. one-to-many attributes).
   *
   * @param entities entities
   */
  <E extends Entity> void add(String entityTypeId, Stream<E> entities);

  /**
   * Updates an entity Note: the caller is responsible for updating the other side of bidirectional
   * relationships (e.g. one-to-many attributes).
   *
   * @throws MolgenisDataException if the repository of the entity isn't an Updateable
   */
  void update(String entityTypeId, Entity entity);

  /**
   * Updates entities Note: the caller is responsible for updating the other side of bidirectional
   * relationships (e.g. one-to-many attributes).
   *
   * @param entities entities
   */
  <E extends Entity> void update(String entityTypeId, Stream<E> entities);

  /**
   * Deletes an entity
   *
   * @throws MolgenisDataException if the repository of the entity isn't an Updateable
   */
  void delete(String entityTypeId, Entity entity);

  /**
   * Delete entities from it's repository
   *
   * @param entities entities
   */
  <E extends Entity> void delete(String entityTypeId, Stream<E> entities);

  /**
   * Deletes an entity by it's id
   *
   * @param id entity id
   */
  void deleteById(String entityTypeId, Object id);

  /**
   * Deletes entities by id
   *
   * @param ids entity ids
   */
  void deleteAll(String entityTypeId, Stream<Object> ids);

  /** Deletes all entities */
  void deleteAll(String entityTypeId);

  /**
   * Returns an untyped query
   *
   * @param entityTypeId entity name
   * @return an untyped query
   */
  Query<Entity> query(String entityTypeId);

  /**
   * Returns a typed query
   *
   * @param entityClass entity class
   * @param <E> entity type
   * @return a typed query
   */
  <E extends Entity> Query<E> query(String entityTypeId, Class<E> entityClass);

  /**
   * Creates counts off all possible combinations of xAttr and yAttr attributes of an entity
   *
   * @param aggregateQuery aggregation query
   * @return aggregation results
   */
  AggregateResult aggregate(String entityTypeId, AggregateQuery aggregateQuery);

  /** Get identifiers of all entity types in this source */
  Stream<String> getEntityTypeIds();
}
