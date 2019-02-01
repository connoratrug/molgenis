package org.molgenis.data.event.sourcig;

import static org.testng.Assert.*;

import java.net.URISyntaxException;
import org.testng.annotations.Test;

public class ExampleModelTest {

  @Test
  public void testCreateModel() throws URISyntaxException {
    ExampleModel exampleModel = new ExampleModel();
    exampleModel.createModel();
  }
}
