/* (C)2020 */
package pers.edison.lazyapp;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.edison.lazyapp.entity.YmlContextEntity;
import pers.edison.lazyapp.exception.AppContextConfigException;
import pers.edison.lazyapp.exception.CircularDependenciesException;
import pers.edison.lazyapp.exception.FailToNewInstanceException;
import pers.edison.lazyapp.exception.RequiredBeanException;
import pers.edison.lazyapp.setting.SettingsCenter;
import pers.edison.lazyapp.worker.BeansFactory;
import pers.edison.lazyapp.worker.ConfigFileParser;

public class LazyBeanApp {

  /** slf4j logger */
  static final Logger logger = LoggerFactory.getLogger(LazyBeanApp.class);

  /** 预加载得到的app对象. 如果没有采用lazyBeanAppLoader预加载. 则该对象始终为null. */
  private static LazyBeanApp preLoadedApp;

  private BeansFactory beans;

  private SettingsCenter settings;

  public LazyBeanApp(String inputConfigUrl)
      throws AppContextConfigException, ClassNotFoundException, CircularDependenciesException,
          RequiredBeanException {
    try {
      load(new String[] {inputConfigUrl});
    } catch (IOException e) {
      logger.warn(e.getMessage(), e);
      throw new AppContextConfigException(e);
    }
  }

  public LazyBeanApp(String[] inputConfigUrls)
      throws AppContextConfigException, ClassNotFoundException, CircularDependenciesException,
          RequiredBeanException {
    try {
      load(inputConfigUrls);
    } catch (IOException e) {
      logger.warn(e.getMessage(), e);
      throw new AppContextConfigException(e);
    }
  }

  public static void setPreloadApp(LazyBeanApp app) {
    preLoadedApp = app;
  }

  public static LazyBeanApp getPreloadApp() {
    return preLoadedApp;
  }

  public HashMap<String, String> getSettings() {
    return settings.getSettings();
  }

  public String getSetting(String key) {
    return settings.getSetting(key);
  }

  public Object getBean(String beanId) throws RequiredBeanException, FailToNewInstanceException {
    return getBean(beanId, false);
  }

  public Object getBean(String beanId, boolean required)
      throws RequiredBeanException, FailToNewInstanceException {
    return beans.getInstance(beanId, required);
  }

  public <T> T getBean(Class<T> klass) throws RequiredBeanException, FailToNewInstanceException {
    return beans.getInstance(klass, null, false);
  }

  public <T> T getBean(Class<T> klass, Class<?> wouldKlass)
      throws RequiredBeanException, FailToNewInstanceException {
    return beans.getInstance(klass, wouldKlass, false);
  }

  public <T> T getBean(Class<T> klass, Class<?> wouldKlass, boolean required)
      throws RequiredBeanException, FailToNewInstanceException {
    return beans.getInstance(klass, wouldKlass, required);
  }

  private void load(String[] inputConfigUrls)
      throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException,
          CircularDependenciesException, RequiredBeanException, AppContextConfigException {
    YmlContextEntity contextEntity = (new ConfigFileParser(inputConfigUrls)).parse();

    settings = (new SettingsCenter(contextEntity)).scanSettings();

    beans = (new BeansFactory(contextEntity, settings)).scanBeans().checkBeans();
  }
}
