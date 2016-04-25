package org.molgenis.data.elasticsearch;

import java.util.Iterator;

import org.molgenis.data.Entity;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.Repository;
import org.molgenis.data.RepositoryCollection;
import org.molgenis.data.transaction.log.index.IndexTransactionLogService;

/**
 * Adds indexing functionality to a RepositoryCollection
 * 
 * TODO This code is still not used!
 */
public class IndexedRepositoryCollectionDecorator implements RepositoryCollection
{
	private final SearchService searchService;
	private RepositoryCollection delegate;
	private IndexTransactionLogService indexTransactionLogService;

	public IndexedRepositoryCollectionDecorator(SearchService searchService, RepositoryCollection delegate,
			IndexTransactionLogService indexTransactionLogService)
	{
		this.searchService = searchService;
		this.delegate = delegate;
		this.indexTransactionLogService = indexTransactionLogService;
	}

	protected IndexedRepositoryCollectionDecorator(SearchService searchService,
			IndexTransactionLogService indexTransactionLogService)
	{
		this(searchService, null, indexTransactionLogService);
	}

	protected void setDelegate(RepositoryCollection delegate)
	{
		this.delegate = delegate;
	}

	protected RepositoryCollection getDelegate()
	{
		return delegate;
	}

	@Override
	public Iterator<Repository<Entity>> iterator()
	{
		return new Iterator<Repository<Entity>>()
		{
			Iterator<Repository<Entity>> it = delegate.iterator();

			@Override
			public boolean hasNext()
			{
				return it.hasNext();
			}

			@Override
			public Repository<Entity> next()
			{
				return new IndexedRepositoryDecorator(it.next(), searchService);
			}

		};
	}

	@Override
	public String getName()
	{
		return delegate.getName();
	}

	@Override
	public Repository<Entity> addEntityMeta(EntityMetaData entityMeta)
	{
		Repository<Entity> repo = delegate.addEntityMeta(entityMeta);
		searchService.createMappings(entityMeta);

		return new IndexedRepositoryDecorator(repo, searchService);
	}

	@Override
	public Iterable<String> getEntityNames()
	{
		return delegate.getEntityNames();
	}

	@Override
	public Repository<Entity> getRepository(String name)
	{
		return new IndexedRepositoryDecorator(delegate.getRepository(name), searchService);
	}

	/**
	 * Get undelying not indexed repository
	 * 
	 * @param name
	 * @return
	 */
	public Repository<Entity> getUnderlying(String name)
	{
		return delegate.getRepository(name);
	}

	protected SearchService getSearchService()
	{
		return searchService;
	}

	@Override
	public boolean hasRepository(String name)
	{
		if (null == name) return false;
		Iterator<String> entityNames = getEntityNames().iterator();
		while (entityNames.hasNext())
		{
			if (entityNames.next().equals(name)) return true;
		}
		return false;
	}

}
