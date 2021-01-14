package org.molgenis.core.ui.controller;

import io.swagger.annotations.Api;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import java.lang.reflect.InvocationTargetException;

@Api("Dynamic")
@Controller
@RequestMapping(DynamicCodeController.ID)
public class DynamicCodeController {
  public static final String ID = "dynamic";

  public DynamicCodeController() {

  }

  @GetMapping(value = "/{number}")
  @ResponseBody
  public String run(@PathVariable(value = "number") String number) throws CompileException, InvocationTargetException {
    System.out.println("Hello dynamic code");

    ExpressionEvaluator ee = new ExpressionEvaluator();
    ee.cook("3 + " + number);
    Object o = ee.evaluate(null);
    System.out.println(o); // Prints "7".

    return o.toString();
  }
}
