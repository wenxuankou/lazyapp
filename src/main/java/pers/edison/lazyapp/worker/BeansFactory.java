/* (C)2020 */
package pers.edison.lazyapp.worker;

import java.io.IOException;
import java.util.HashMap;
import pers.edison.lazyapp.entity.YmlBeanEntity;
import pers.edison.lazyapp.entity.YmlContextEntity;
import pers.edison.lazyapp.exception.CircularDependenciesException;
import pers.edison.lazyapp.exception.FailToNewInstanceException;
import pers.edison.lazyapp.exception.RequiredBeanException;
import pers.edison.lazyapp.setting.SettingsCenter;

public class BeansFactory {

  private HashMap<String, YmlBeanEntity> beans;

  private SettingsCenter settingsCenter;

  private YmlContextEntity contextEntity;

  private IBeansProspector prospector;

  public BeansFactory(YmlContextEntity contextEntity, SettingsCenter settingsCenter) {
    this.contextEntity = contextEntity;
    this.settingsCenter = settingsCenter;

    this.beans = new HashMap<String, YmlBeanEntity>();
  }

  public BeansFactory scanBeans() throws ClassNotFoundException, IOException {
    String[] pkgs = contextEntity.getScanners();
    YmlBeanEntity[] beanEntities = contextEntity.getBeans();

    (new BeansScanner(this)).scanBeans(pkgs, beanEntities);

    this.prospector = new BeansProspector(this);

    return this;
  }

  public BeansFactory checkBeans()
      throws ClassNotFoundException, CircularDependenciesException, RequiredBeanException {
    (new BeansChecker(this)).checkBeans();

    return this;
  }

  public Object getInstance(String beanId, boolean required)
      throws RequiredBeanException, FailToNewInstanceException {
    return (new BeansProducer(this)).getInstance(beanId, required);
  }

  public <T> T getInstance(Class<T> klass, Class<?> wouldKlass, boolean required)
      throws RequiredBeanException, FailToNewInstanceException {
    return (new BeansProducer(this)).getInstance(klass, wouldKlass, required);
  }

  public HashMap<String, YmlBeanEntity> getBeans() {
    return beans;
  }

  public void setBeans(HashMap<String, YmlBeanEntity> beans) {
    this.beans = beans;
  }

  public IBeansProspector getProspector() {
    return prospector;
  }

  public void setProspector(IBeansProspector prospector) {
    this.prospector = prospector;
  }

  public SettingsCenter getSettingsCenter() {
    return settingsCenter;
  }

  public void setSettingsCenter(SettingsCenter settingsCenter) {
    this.settingsCenter = settingsCenter;
  }
}
