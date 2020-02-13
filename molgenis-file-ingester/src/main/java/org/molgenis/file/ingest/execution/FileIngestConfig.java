package org.molgenis.file.ingest.execution;

import static com.google.common.collect.ImmutableMap.of;
import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.molgenis.file.ingest.meta.FileIngestJobExecution;
import org.molgenis.file.ingest.meta.FileIngestJobExecutionMetadata;
import org.molgenis.jobs.Job;
import org.molgenis.jobs.JobFactory;
import org.molgenis.jobs.model.ScheduledJobType;
import org.molgenis.jobs.model.ScheduledJobTypeFactory;
import org.molgenis.web.menu.MenuReaderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@Import(FileIngester.class)
public class FileIngestConfig {
  private final FileIngester fileIngester;
  private final ScheduledJobTypeFactory scheduledJobTypeFactory;
  private final FileIngestJobExecutionMetadata fileIngestJobExecutionMetadata;
  private final MenuReaderService menuReaderService;
  private final Gson gson;

  public FileIngestConfig(
      FileIngester fileIngester,
      ScheduledJobTypeFactory scheduledJobTypeFactory,
      FileIngestJobExecutionMetadata fileIngestJobExecutionMetadata,
      MenuReaderService menuReaderService,
      Gson gson) {
    this.fileIngester = requireNonNull(fileIngester);
    this.scheduledJobTypeFactory = requireNonNull(scheduledJobTypeFactory);
    this.fileIngestJobExecutionMetadata = requireNonNull(fileIngestJobExecutionMetadata);
    this.menuReaderService = requireNonNull(menuReaderService);
    this.gson = requireNonNull(gson);
  }

  /** The FileIngestJob Factory bean. */
  @Bean
  public JobFactory<FileIngestJobExecution> fileIngestJobFactory() {
    return new JobFactory<FileIngestJobExecution>() {
      @Override
      public Job createJob(FileIngestJobExecution fileIngestJobExecution) {
        final String targetEntityId = fileIngestJobExecution.getTargetEntityId();
        final String url = fileIngestJobExecution.getUrl();
        final String loader = fileIngestJobExecution.getLoader();
        String dataExplorerURL = menuReaderService.findMenuItemPath("dataexplorer");
        fileIngestJobExecution.setResultUrl(
            format("{0}?entity={1}", dataExplorerURL, targetEntityId));
        return progress ->
            fileIngester.ingest(
                targetEntityId, url, loader, fileIngestJobExecution.getIdentifier(), progress);
      }
    };
  }

  @SuppressWarnings("java:S1192") // String literals should not be duplicated
  @Lazy
  @Bean
  public ScheduledJobType fileIngestJobType() {
    ScheduledJobType result = scheduledJobTypeFactory.create("fileIngest");
    result.setLabel("File ingest");
    result.setDescription("This job downloads a file from a URL and imports it into MOLGENIS.");
    result.setSchema(
        gson.toJson(
            of(
                "title",
                "FileIngest Job",
                "type",
                "object",
                "properties",
                of(
                    "url",
                    of(
                        "type",
                        "string",
                        "format",
                        "uri",
                        "description",
                        "URL to download the file to ingest from"),
                    "loader",
                    of(
                        "enum",
                        ImmutableList.of("CSV"),
                        "description",
                        "Loader used to ingest the file"),
                    "targetEntityId",
                    of("type", "string", "description", "ID of the entity to import to")),
                "required",
                ImmutableList.of("url", "loader", "targetEntityId"))));
    result.setJobExecutionType(fileIngestJobExecutionMetadata);
    return result;
  }
}
