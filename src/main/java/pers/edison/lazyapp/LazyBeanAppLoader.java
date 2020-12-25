/* (C)2020 */
package pers.edison.lazyapp;

import pers.edison.lazyapp.exception.AppContextConfigException;
import pers.edison.lazyapp.exception.CircularDependenciesException;
import pers.edison.lazyapp.exception.RequiredBeanException;

public class LazyBeanAppLoader {

  public static final String defaultConfigFileName = "lazyAppContext.yml";

  public LazyBeanAppLoader()
      throws AppContextConfigException, ClassNotFoundException, CircularDependenciesException,
          RequiredBeanException {
    load(new String[] {defaultConfigFileName});
  }

  public LazyBeanAppLoader(String inputConfigUrl)
      throws AppContextConfigException, ClassNotFoundException, CircularDependenciesException,
          RequiredBeanException {
    load(new String[] {inputConfigUrl});
  }

  public LazyBeanAppLoader(String[] inputConfigUrls)
      throws AppContextConfigException, ClassNotFoundException, CircularDependenciesException,
          RequiredBeanException {
    load(inputConfigUrls);
  }

  public void load(String[] inputConfigUrls)
      throws AppContextConfigException, ClassNotFoundException, CircularDependenciesException,
          RequiredBeanException {
    LazyBeanApp app = new LazyBeanApp(inputConfigUrls);
    LazyBeanApp.setPreloadApp(app);
  }
}
