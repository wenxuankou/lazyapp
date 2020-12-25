/* (C)2020 */
package pers.edison.lazyapp.worker;

import pers.edison.lazyapp.entity.YmlBeanEntity;
import pers.edison.lazyapp.exception.RequiredBeanException;

public interface IBeansProspector {

  public YmlBeanEntity find(String beanId) throws RequiredBeanException;

  public YmlBeanEntity find(String beanId, boolean required) throws RequiredBeanException;

  public YmlBeanEntity find(Class<?> klass) throws RequiredBeanException;

  public YmlBeanEntity find(Class<?> klass, Class<?> wouldKlass) throws RequiredBeanException;

  public YmlBeanEntity find(Class<?> klass, Class<?> wouldKlass, boolean required)
      throws RequiredBeanException;
}
