package org.molgenis.ui;

import org.molgenis.auth.MolgenisUserDecorator;
import org.molgenis.auth.MolgenisUserMetaData;
import org.molgenis.data.AutoValueRepositoryDecorator;
import org.molgenis.data.ComputedEntityValuesDecorator;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityManager;
import org.molgenis.data.EntityReferenceResolverDecorator;
import org.molgenis.data.IdGenerator;
import org.molgenis.data.Repository;
import org.molgenis.data.RepositoryDecoratorFactory;
import org.molgenis.data.RepositorySecurityDecorator;
import org.molgenis.data.elasticsearch.IndexedRepositoryDecorator;
import org.molgenis.data.elasticsearch.SearchService;
import org.molgenis.data.settings.AppSettings;
import org.molgenis.data.support.OwnedEntityMetaData;
import org.molgenis.data.transaction.log.index.IndexTransactionLogRepositoryDecorator;
import org.molgenis.data.transaction.log.index.IndexTransactionLogService;
import org.molgenis.data.validation.EntityAttributesValidator;
import org.molgenis.data.validation.ExpressionValidator;
import org.molgenis.data.validation.RepositoryValidationDecorator;
import org.molgenis.security.owned.OwnedEntityRepositoryDecorator;
import org.molgenis.util.EntityUtils;

public class MolgenisRepositoryDecoratorFactory implements RepositoryDecoratorFactory
{
	private final EntityManager entityManager;
	private final IndexTransactionLogService indexTansactionLogService;
	private final EntityAttributesValidator entityAttributesValidator;
	private final IdGenerator idGenerator;
	private final AppSettings appSettings;
	private final DataService dataService;
	private final ExpressionValidator expressionValidator;
	private final RepositoryDecoratorRegistry repositoryDecoratorRegistry;
	private final SearchService searchService;

	public MolgenisRepositoryDecoratorFactory(EntityManager entityManager,
			EntityAttributesValidator entityAttributesValidator, IdGenerator idGenerator, AppSettings appSettings,
			DataService dataService, ExpressionValidator expressionValidator,
			RepositoryDecoratorRegistry repositoryDecoratorRegistry,
			IndexTransactionLogService indexTansactionLogService, SearchService searchService)
	{
		this.entityManager = entityManager;
		this.entityAttributesValidator = entityAttributesValidator;
		this.idGenerator = idGenerator;
		this.appSettings = appSettings;
		this.dataService = dataService;
		this.expressionValidator = expressionValidator;
		this.repositoryDecoratorRegistry = repositoryDecoratorRegistry;
		this.indexTansactionLogService = indexTansactionLogService;
		this.searchService = searchService;
	}

	@Override
	public Repository<Entity> createDecoratedRepository(Repository<Entity> repository)
	{
		Repository<Entity> decoratedRepository = repositoryDecoratorRegistry.decorate(repository);

		if (decoratedRepository.getName().equals(MolgenisUserMetaData.ENTITY_NAME))
		{
			decoratedRepository = new MolgenisUserDecorator(decoratedRepository);
		}

		// 8. Owned decorator
		if (EntityUtils.doesExtend(decoratedRepository.getEntityMetaData(), OwnedEntityMetaData.ENTITY_NAME))
		{
			decoratedRepository = new OwnedEntityRepositoryDecorator(decoratedRepository);
		}

		// 7. Entity reference resolver decorator
		decoratedRepository = new EntityReferenceResolverDecorator(decoratedRepository, entityManager);

		// 6. Computed entity values decorator
		decoratedRepository = new ComputedEntityValuesDecorator(decoratedRepository);

		// 5. Entity listener
		decoratedRepository = new EntityListenerRepositoryDecorator(decoratedRepository);

		// 4. Index

		// Index query analyzer
		decoratedRepository = new IndexedRepositoryDecorator(decoratedRepository, searchService);

		// Index transaction log
		decoratedRepository = new IndexTransactionLogRepositoryDecorator(decoratedRepository, indexTansactionLogService);

		// 3. validation decorator
		decoratedRepository = new RepositoryValidationDecorator(dataService, decoratedRepository,
				entityAttributesValidator, expressionValidator);

		// 2. auto value decorator
		decoratedRepository = new AutoValueRepositoryDecorator(decoratedRepository, idGenerator);

		// 1. security decorator
		decoratedRepository = new RepositorySecurityDecorator(decoratedRepository, appSettings);

		return decoratedRepository;
	}
}
