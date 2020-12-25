/* (C)2020 */
package pers.edison.lazyapp.policy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import pers.edison.lazyapp.annotation.LazyInject;
import pers.edison.lazyapp.entity.YmlBeanEntity;
import pers.edison.lazyapp.entity.YmlParamEntity;
import pers.edison.lazyapp.entity.YmlPropertyEntity;
import pers.edison.lazyapp.exception.CircularDependenciesException;
import pers.edison.lazyapp.exception.RequiredBeanException;
import pers.edison.lazyapp.worker.IBeansProspector;

public class CheckBeanPolicy implements ICheckBeanPolicy {

  private IBeansProspector prospector;

  @Override
  public void check(YmlBeanEntity beanEntity, IBeansProspector prospector)
      throws ClassNotFoundException, CircularDependenciesException, RequiredBeanException {
    this.prospector = prospector;

    checkCircularDependencies(beanEntity);
  }

  private void checkCircularDependencies(YmlBeanEntity beanEntity)
      throws CircularDependenciesException, ClassNotFoundException, RequiredBeanException {
    List<List<String>> dependencyMesh = null;

    dependencyMesh = initDependencyMesh(beanEntity);

    for (List<String> list : dependencyMesh) {
      Set<String> set = new HashSet<String>(list);

      if (set.size() != list.size()) {
        throw new CircularDependenciesException();
      }
    }
  }

  private List<List<String>> initDependencyMesh(YmlBeanEntity beanEntity)
      throws ClassNotFoundException, RequiredBeanException {
    List<List<String>> mesh = new LinkedList<List<String>>();

    meshCrawler(beanEntity, "", mesh);

    return mesh;
  }

  private void meshCrawler(YmlBeanEntity beanEntity, String crawlerLead, List<List<String>> mesh)
      throws ClassNotFoundException, RequiredBeanException {
    int isCyclic = crawlerLead.indexOf(beanEntity.getClassName());

    crawlerLead = crawlerLead + "|" + beanEntity.getClassName();

    if (isCyclic < 0) {
      if (beanEntity.isManually()) {
        meshCrawlerByConfig(beanEntity, crawlerLead, mesh);
      } else {
        meshCrawlerByAnnotation(beanEntity.getKlass(), crawlerLead, mesh);
      }
    }

    String[] dependencyArray = crawlerLead.split("\\|");

    List<String> dependencyList = new ArrayList<String>(Arrays.asList(dependencyArray));

    dependencyList.remove("");

    mesh.add(dependencyList);
  }

  private void meshCrawlerByConfig(
      YmlBeanEntity beanEntity, String crawlerLead, List<List<String>> mesh)
      throws ClassNotFoundException, RequiredBeanException {
    if (Objects.nonNull(beanEntity.getProperties())) {
      meshCrawlerBySetterConfig(beanEntity.getProperties(), crawlerLead, mesh);
    }

    if (Objects.nonNull(beanEntity.getParams())) {
      meshCrawlerByConstructorConfig(beanEntity.getParams(), crawlerLead, mesh);
    }
  }

  private void meshCrawlerBySetterConfig(
      YmlPropertyEntity[] props, String crawlerLead, List<List<String>> mesh)
      throws ClassNotFoundException, RequiredBeanException {
    for (YmlPropertyEntity propertyEntity : props) {
      if (propertyEntity.getRef() != null) {
        YmlBeanEntity nextBeanEntity = prospector.find(propertyEntity.getRef().substring(1), true);

        if (nextBeanEntity != null) {
          meshCrawler(nextBeanEntity, crawlerLead, mesh);
        }
      }
    }
  }

  private void meshCrawlerByConstructorConfig(
      YmlParamEntity[] params, String crawlerLead, List<List<String>> mesh)
      throws ClassNotFoundException, RequiredBeanException {
    for (YmlParamEntity paramEntity : params) {
      if (paramEntity.getRef() != null) {
        YmlBeanEntity nextBeanEntity = prospector.find(paramEntity.getRef().substring(1), true);

        if (nextBeanEntity != null) {
          meshCrawler(nextBeanEntity, crawlerLead, mesh);
        }
      }
    }
  }

  private void meshCrawlerByAnnotation(Class<?> klass, String crawlerLead, List<List<String>> mesh)
      throws ClassNotFoundException, RequiredBeanException {
    Constructor<?>[] constructArray = klass.getDeclaredConstructors();
    Field[] fieldArray = klass.getDeclaredFields();

    Constructor<?> lazyInjectConstructor = null;

    for (Constructor<?> c : constructArray) {
      LazyInject lazyInject = c.getAnnotation(LazyInject.class);

      if (Objects.nonNull(lazyInject)) {
        lazyInjectConstructor = c;
        break;
      }
    }

    if (lazyInjectConstructor != null) {
      meshCrawlerByConstructor(lazyInjectConstructor, crawlerLead, mesh);
    } else {
      meshCrawlerBySetter(fieldArray, crawlerLead, mesh);
    }
  }

  private void meshCrawlerBySetter(Field[] fieldArray, String crawlerLead, List<List<String>> mesh)
      throws ClassNotFoundException, RequiredBeanException {
    for (Field f : fieldArray) {
      LazyInject lazyInject = f.getAnnotation(LazyInject.class);

      if (Objects.isNull(lazyInject)) {
        continue;
      }

      YmlBeanEntity nextBeanEntity = null;

      if ("".equals(lazyInject.value())) {
        nextBeanEntity = prospector.find(f.getType(), null, lazyInject.require());
      } else {
        Class<?> wouldKlass = Class.forName(lazyInject.value());
        nextBeanEntity = prospector.find(f.getType(), wouldKlass, lazyInject.require());
      }

      if (nextBeanEntity != null) {
        meshCrawler(nextBeanEntity, crawlerLead, mesh);
      }
    }
  }

  private void meshCrawlerByConstructor(
      Constructor<?> constructor, String crawlerLead, List<List<String>> mesh)
      throws ClassNotFoundException, RequiredBeanException {
    LazyInject lazyInject = constructor.getAnnotation(LazyInject.class);

    Class<?>[] paramTypes = constructor.getParameterTypes();

    for (Class<?> paramType : paramTypes) {
      YmlBeanEntity nextBeanEntity = prospector.find(paramType, null, lazyInject.require());

      if (Objects.nonNull(nextBeanEntity)) {
        meshCrawler(nextBeanEntity, crawlerLead, mesh);
      }
    }
  }
}
