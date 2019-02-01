package org.molgenis.data.event.sourcig;

import static org.apache.jena.Jena.PATH;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.codec.binary.Base64;


public class ExampleModel {

  private static final String CONTENT_TYPE_TURTLE = RDFLanguages.TURTLE.getContentType().getContentType();
  private static final String TRIPLE_STORE_BASE_URI = "http://localhost:3030";
  private static final String EVENT_SOURCE_CREATOR_HEADER = "X-EventSource-Creator";
  private static final String EVENT_SOURCE_TITLE_HEADER = "X-EventSource-Title";
  private static final String INITIAL_COMMIT_MESSAGE = "Dataset created through molgenis es demo";
  private static final String DATA_SETS_END_POINT = "/datasets";
  private static final Logger LOG = LoggerFactory.getLogger(ExampleModel.class);

  private RestTemplate restTemplate = new RestTemplate();

  public void createModel() throws URISyntaxException {
    String datasetUri = "http://molgenis.org/datasets/esdemods";
    Model baseDatasetModel = buildDatasetBaseModel("esdemo", "Demo dataset", datasetUri);
    String triples = modelToString(baseDatasetModel);
    createDataset("demo-creator", datasetUri, triples);
  }

  private URI createDataset(String creatorName, String datasetUri, String defaultGraphContent) throws URISyntaxException {
    HttpHeaders httpHeaders = createEventSourcingHeaders(creatorName, INITIAL_COMMIT_MESSAGE,
        CONTENT_TYPE_TURTLE);
    HttpEntity<String> requestEntity = new HttpEntity<>(defaultGraphContent, httpHeaders);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(TRIPLE_STORE_BASE_URI + DATA_SETS_END_POINT, requestEntity, String.class);
      if (!HttpStatus.CREATED.equals(response.getStatusCode())) {
        LOG.error("error , could not create dataset, tripleStore response = {}", response.getStatusCode().getReasonPhrase());
        throw new Exception();
      }
      URI location = response.getHeaders().getLocation();

      LOG.info("dataset created at {} by {} with uri {}", location, creatorName, datasetUri);
    } catch (Exception e) {
      LOG.error("Error creating dataset", e);
    }
    return new URI(datasetUri);
  }

  private HttpHeaders createEventSourcingHeaders(String creator, String commitTitle, String contentType) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(EVENT_SOURCE_CREATOR_HEADER, "mailto:" + creator + "@molgenis.com");
    httpHeaders.add(EVENT_SOURCE_TITLE_HEADER, Base64.encodeBase64String(commitTitle.getBytes()));
    httpHeaders.add(CONTENT_TYPE, contentType);
    return httpHeaders;
  }

  private Model buildDatasetBaseModel(String title, String description, String datasetUri) {
    Model model = ModelFactory.createDefaultModel();
    Resource resource = model.createResource(datasetUri);
    resource.addProperty(DCTerms.title, title);
    if (!Strings.isEmpty(description)) {
      resource.addProperty(DCTerms.description, description);
    }

    return model;
  }

  private String modelToString(Model model) {
    StringWriter outputWriter = new StringWriter();
    model.write(outputWriter, "Turtle");
    return outputWriter.toString();
  }
}
