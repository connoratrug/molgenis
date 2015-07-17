package org.molgenis.ontology.roc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.QueryRule;
import org.molgenis.data.QueryRule.Operator;
import org.molgenis.data.semanticsearch.string.NGramDistanceAlgorithm;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.ontology.core.meta.OntologyMetaData;
import org.molgenis.ontology.core.meta.OntologyTermMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.tartarus.snowball.ext.PorterStemmer;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.UncheckedExecutionException;

public class InformationContentService
{
	private static final String NON_WORD_SEPARATOR = "[^a-zA-Z0-9]";
	private static final String SINGLE_WHITESPACE = " ";

	private final LoadingCache<String, Long> CACHED_TOTAL_WORD_COUNT = CacheBuilder.newBuilder()
			.maximumSize(Integer.MAX_VALUE).expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Long>()
			{
				@Override
				public Long load(String ontologyIri)
				{
					Entity ontologyEntity = dataService.findOne(OntologyMetaData.ENTITY_NAME,
							new QueryImpl().eq(OntologyMetaData.ONTOLOGY_IRI, ontologyIri));
					if (ontologyEntity != null)
					{
						return dataService.count(OntologyTermMetaData.ENTITY_NAME,
								new QueryImpl().eq(OntologyTermMetaData.ONTOLOGY, ontologyEntity));
					}
					return (long) 0;
				}
			});
	private final LoadingCache<OntologyWord, Double> CACHED_INVERSE_DOCUMENT_FREQ = CacheBuilder.newBuilder()
			.maximumSize(Integer.MAX_VALUE).expireAfterWrite(1, TimeUnit.DAYS)
			.build(new CacheLoader<OntologyWord, Double>()
			{
				public Double load(OntologyWord key) throws ExecutionException
				{
					String ontologyIri = key.getOntologyIri();
					Entity ontologyEntity = dataService.findOne(OntologyMetaData.ENTITY_NAME,
							new QueryImpl().eq(OntologyMetaData.ONTOLOGY_IRI, ontologyIri));
					if (ontologyEntity != null)
					{
						QueryRule queryRule = new QueryRule(Arrays.asList(new QueryRule(
								OntologyTermMetaData.ONTOLOGY_TERM_SYNONYM, Operator.FUZZY_MATCH, key.getWord())));
						queryRule.setOperator(Operator.DIS_MAX);
						long wordCount = dataService.count(OntologyTermMetaData.ENTITY_NAME, new QueryImpl(queryRule));
						Long total = CACHED_TOTAL_WORD_COUNT.get(ontologyIri);
						BigDecimal idfValue = new BigDecimal(total == null ? 0 : (1 + Math.log((double) total
								/ (wordCount + 1))));
						return idfValue.doubleValue();
					}
					return (double) 0;
				}
			});

	private final DataService dataService;

	@Autowired
	public InformationContentService(DataService dataService)
	{
		if (dataService == null) throw new IllegalArgumentException("DataService is null!");
		this.dataService = dataService;
	}

	public Map<String, Double> redistributedNGramScore(String queryString, String ontologyIri)
	{
		Map<String, Double> wordIDFMap = createWordIDF(queryString, ontologyIri);
		Map<String, Double> wordWeightedSimilarity = new HashMap<String, Double>();

		if (wordIDFMap.size() > 0)
		{
			double averageIDFValue = wordIDFMap.values().stream().mapToDouble(Double::doubleValue).average()
					.getAsDouble();
			double queryStringLength = StringUtils.join(createStemmedWordSet(queryString), SINGLE_WHITESPACE).trim()
					.length();
			double totalContribution = 0;
			double totalDenominator = 0;

			for (Entry<String, Double> entry : wordIDFMap.entrySet())
			{
				double diff = entry.getValue() - averageIDFValue;
				if (diff < 0)
				{
					Double contributedSimilarity = (entry.getKey().length() / queryStringLength * 100)
							* (diff / averageIDFValue);
					totalContribution += Math.abs(contributedSimilarity);
					wordWeightedSimilarity.put(entry.getKey(), contributedSimilarity);
				}
				else
				{
					totalDenominator += diff;
				}
			}

			for (Entry<String, Double> entry : wordIDFMap.entrySet())
			{
				double diff = entry.getValue() - averageIDFValue;
				if (diff > 0)
				{
					wordWeightedSimilarity.put(entry.getKey(), ((diff / totalDenominator) * totalContribution));
				}
			}
		}
		return wordWeightedSimilarity;
	}

	Map<String, Double> createWordIDF(String queryString, String ontologyIri)
	{
		Map<String, Double> wordFreqMap = new HashMap<String, Double>();
		Set<String> wordsInQueryString = createStemmedWordSet(queryString);
		wordsInQueryString.stream().forEach(word -> {
			Double wordIDF = null;
			try
			{
				wordIDF = CACHED_INVERSE_DOCUMENT_FREQ.get(new OntologyWord(ontologyIri, word));
			}
			catch (ExecutionException e)
			{
				throw new UncheckedExecutionException(e);
			}

			if (wordIDF != null && wordIDF != 0)
			{
				wordFreqMap.put(word, wordIDF);
			}
		});
		return wordFreqMap;
	}

	public Set<String> createStemmedWordSet(String queryString)
	{
		PorterStemmer stemmer = new PorterStemmer();
		Set<String> uniqueTerms = FluentIterable
				.from(Sets.newHashSet(queryString.toLowerCase().trim().split(NON_WORD_SEPARATOR)))
				.filter(new Predicate<String>()
				{
					public boolean apply(String termBeforeStem)
					{
						return !Iterables.contains(NGramDistanceAlgorithm.STOPWORDSLIST, termBeforeStem);
					}
				}).transform(new Function<String, String>()
				{
					public String apply(String term)
					{
						stemmer.setCurrent(term);
						stemmer.stem();
						return stemmer.getCurrent();
					}
				}).filter(new Predicate<String>()
				{
					public boolean apply(String termAfterStem)
					{
						return StringUtils.isNotEmpty(termAfterStem);
					}
				}).toSet();

		return uniqueTerms;
	}
}