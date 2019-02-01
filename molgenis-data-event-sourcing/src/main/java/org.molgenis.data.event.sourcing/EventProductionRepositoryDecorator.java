package org.molgenis.data.event.sourcing;

import java.util.stream.Stream;
import org.molgenis.data.AbstractRepositoryDecorator;
import org.molgenis.data.Entity;
import org.molgenis.data.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventProductionRepositoryDecorator extends AbstractRepositoryDecorator<Entity> {

  private static final Logger LOG =
      LoggerFactory.getLogger(EventProductionRepositoryDecorator.class);

  public EventProductionRepositoryDecorator(Repository<Entity> delegateRepository) {
    super(delegateRepository);
  }

  @Override
  public void add(Entity entity) {
    LOG.info(
        "adding entity:'{}' of type '{}'", entity.getIdValue(), entity.getEntityType().getId());
    super.add(entity);
  }

  @Override
  public void update(Entity entity) {
    LOG.info(
        "updating entity:'{}' of type '{}'", entity.getIdValue(), entity.getEntityType().getId());
    super.update(entity);
  }

  @Override
  public void delete(Entity entity) {
    LOG.info(
        "deleting entity:'{}' of type '{}'", entity.getIdValue(), entity.getEntityType().getId());
    super.delete(entity);
  }

  @Override
  public void deleteById(Object id) {
    LOG.info("deleting entity by id:'{}' ", id);
    super.deleteById(id);
  }

  @Override
  public void deleteAll() {
    LOG.info("deleting all");
    super.deleteAll();
  }

  @Override
  public Integer add(Stream<Entity> entities) {
    LOG.info("add stream of entities");
    return super.add(entities);
  }

  @Override
  public void update(Stream<Entity> entities) {
    LOG.info("update stream of entities");
    super.update(entities);
  }

  @Override
  public void delete(Stream<Entity> entities) {
    LOG.info("delete entities");
    super.delete(entities);
  }

  @Override
  public void deleteAll(Stream<Object> ids) {
    LOG.info("add stream of ids");
    super.deleteAll(ids);
  }
}
