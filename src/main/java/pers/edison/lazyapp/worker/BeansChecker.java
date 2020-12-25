/* (C)2020 */
package pers.edison.lazyapp.worker;

import pers.edison.lazyapp.entity.YmlBeanEntity;
import pers.edison.lazyapp.exception.CircularDependenciesException;
import pers.edison.lazyapp.exception.RequiredBeanException;
import pers.edison.lazyapp.policy.CheckBeanPolicy;
import pers.edison.lazyapp.policy.ICheckBeanPolicy;

public class BeansChecker {

  private BeansFactory beansFactory;

  public BeansChecker(BeansFactory beansFactory) {
    this.beansFactory = beansFactory;
  }

  public void checkBeans()
      throws ClassNotFoundException, CircularDependenciesException, RequiredBeanException {
    ICheckBeanPolicy policy = new CheckBeanPolicy();

    for (YmlBeanEntity entity : beansFactory.getBeans().values()) {
      policy.check(entity, beansFactory.getProspector());
    }
  }
}
