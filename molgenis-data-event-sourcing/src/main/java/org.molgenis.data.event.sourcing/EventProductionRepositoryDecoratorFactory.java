package org.molgenis.data.event.sourcing;

import java.util.Map;
import org.molgenis.data.Repository;
import org.molgenis.data.decorator.DynamicRepositoryDecoratorFactory;
import org.springframework.stereotype.Component;

@Component
public class EventProductionRepositoryDecoratorFactory
    implements DynamicRepositoryDecoratorFactory {

  @Override
  public String getId() {
    return "event-producer";
  }

  @Override
  public String getLabel() {
    return "Event producer";
  }

  @Override
  public String getDescription() {
    return "It produces the events";
  }

  @Override
  public String getSchema() {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Repository createDecoratedRepository(Repository repository, Map parameters) {
    return new EventProductionRepositoryDecorator(repository);
  }
}
