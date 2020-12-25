/* (C)2020 */
package pers.edison.lazyapp.policy;

import pers.edison.lazyapp.entity.YmlBeanEntity;
import pers.edison.lazyapp.exception.CircularDependenciesException;
import pers.edison.lazyapp.exception.RequiredBeanException;
import pers.edison.lazyapp.worker.IBeansProspector;

public interface ICheckBeanPolicy {

  public void check(YmlBeanEntity beanEntity, IBeansProspector prospector)
      throws ClassNotFoundException, CircularDependenciesException, RequiredBeanException;
}
