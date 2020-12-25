/* (C)2020 */
package pers.edison.lazyapp.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import pers.edison.lazyapp.entity.YmlBeanEntity;
import pers.edison.lazyapp.exception.RequiredBeanException;

public class BeansProspector implements IBeansProspector {

  private HashMap<String, YmlBeanEntity> beans;

  public BeansProspector(BeansFactory beansFactory) {
    this.beans = beansFactory.getBeans();
  }

  @Override
  public YmlBeanEntity find(String beanId) throws RequiredBeanException {
    return find(beanId, false);
  }

  @Override
  public YmlBeanEntity find(String beanId, boolean required) throws RequiredBeanException {
    YmlBeanEntity bean = beans.get(beanId);

    if (required && bean == null) {
      throw new RequiredBeanException(beanId);
    }

    return bean;
  }

  @Override
  public YmlBeanEntity find(Class<?> klass) throws RequiredBeanException {
    return find(klass, null, false);
  }

  @Override
  public YmlBeanEntity find(Class<?> klass, Class<?> wouldKlass) throws RequiredBeanException {
    return find(klass, wouldKlass, false);
  }

  @Override
  public YmlBeanEntity find(Class<?> klass, Class<?> wouldKlass, boolean required)
      throws RequiredBeanException {
    ArrayList<YmlBeanEntity> matchedClasses = binaryFilter(klass);

    if (wouldKlass != null) {
      Iterator<YmlBeanEntity> it = matchedClasses.iterator();

      while (it.hasNext()) {
        YmlBeanEntity bean = it.next();

        if (!bean.getClassName().equals(wouldKlass.getName())) {
          it.remove();
        }
      }
    }

    if (required && matchedClasses.isEmpty()) {
      throw new RequiredBeanException(klass.getName());
    }

    return matchedClasses.get(0);
  }

  private ArrayList<YmlBeanEntity> binaryFilter(Class<?> klass) {
    ArrayList<YmlBeanEntity> matchedClasses = new ArrayList<YmlBeanEntity>();

    for (YmlBeanEntity bean : beans.values()) {
      if (klass.isAssignableFrom(bean.getKlass())) {
        matchedClasses.add(bean);
      }
    }

    return matchedClasses;
  }
}
