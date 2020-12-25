/* (C)2020 */
package pers.edison.lazyapp.worker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.edison.lazyapp.annotation.LazyInject;
import pers.edison.lazyapp.annotation.lazyConfigInject;
import pers.edison.lazyapp.entity.YmlBeanEntity;
import pers.edison.lazyapp.entity.YmlParamEntity;
import pers.edison.lazyapp.entity.YmlPropertyEntity;
import pers.edison.lazyapp.exception.FailToNewInstanceException;
import pers.edison.lazyapp.exception.RequiredBeanException;
import pers.edison.lazyapp.setting.SettingsCenter;

public class BeansProducer {

  /** slf4j logger */
  static final Logger logger = LoggerFactory.getLogger(BeansProducer.class);

  private SettingsCenter settings;

  private IBeansProspector prospector;

  /** 基本类型与包装类型的映射 */
  private static HashMap<String, String> basicTypesMapping = new HashMap<String, String>();

  static {
    basicTypesMapping.put("short", "java.lang.Short");
    basicTypesMapping.put("char", "java.lang.Character");
    basicTypesMapping.put("int", "java.lang.Integer");
    basicTypesMapping.put("long", "java.lang.Long");
    basicTypesMapping.put("float", "java.lang.Float");
    basicTypesMapping.put("double", "java.lang.Double");
    basicTypesMapping.put("boolean", "java.lang.Boolean");
  }

  public BeansProducer(BeansFactory beansFactory) {
    this.settings = beansFactory.getSettingsCenter();
    this.prospector = beansFactory.getProspector();
  }

  public Object getInstance(String beanId)
      throws RequiredBeanException, FailToNewInstanceException {
    return getInstance(beanId, false);
  }

  public Object getInstance(String beanId, boolean required)
      throws RequiredBeanException, FailToNewInstanceException {
    YmlBeanEntity bean = prospector.find(beanId, required);

    return makeInstance(bean);
  }

  public <T> T getInstance(Class<T> klass)
      throws RequiredBeanException, FailToNewInstanceException {

    return getInstance(klass, null, false);
  }

  public <T> T getInstance(Class<T> klass, Class<?> wouldKlass)
      throws RequiredBeanException, FailToNewInstanceException {

    return getInstance(klass, wouldKlass, false);
  }

  @SuppressWarnings("unchecked")
  public <T> T getInstance(Class<T> klass, Class<?> wouldKlass, boolean required)
      throws RequiredBeanException, FailToNewInstanceException {
    YmlBeanEntity bean = prospector.find(klass, wouldKlass, required);

    return (T) makeInstance(bean);
  }

  private Object makeInstance(YmlBeanEntity bean)
      throws RequiredBeanException, FailToNewInstanceException {
    if (bean == null) {
      return null;
    }

    try {
      if (bean.isManually()) {
        return makeInstanceByConfig(bean);
      } else {
        return makeInstanceByAnnotation(bean);
      }
    } catch (NoSuchMethodException
        | SecurityException
        | InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | ClassNotFoundException e) {
      logger.warn(e.getMessage(), e);
      throw new FailToNewInstanceException(bean.getId(), e);
    }
  }

  private Object makeInstanceByConfig(YmlBeanEntity bean)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException,
          InvocationTargetException, NoSuchMethodException, SecurityException,
          ClassNotFoundException, RequiredBeanException, FailToNewInstanceException {
    if (Objects.nonNull(bean.getParams())) {
      return makeInstanceByConstructorConfig(bean);
    }

    return makeInstanceBySetterConfig(bean);
  }

  private Object makeInstanceBySetterConfig(YmlBeanEntity bean)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException,
          InvocationTargetException, NoSuchMethodException, SecurityException,
          ClassNotFoundException, RequiredBeanException, FailToNewInstanceException {
    YmlPropertyEntity[] props = bean.getProperties();

    Object obj = bean.getKlass().getDeclaredConstructor().newInstance();

    for (YmlPropertyEntity prop : props) {
      String setMethod =
          "set" + prop.getName().substring(0, 1).toUpperCase() + prop.getName().substring(1);

      Method m =
          bean.getKlass()
              .getMethod(
                  setMethod, new Class<?>[] {Class.forName(realTypeMapping(prop.getType()))});

      if (prop.getValue() != null) {
        m.invoke(obj, new Object[] {basicTypeConvert(prop.getValue(), prop.getType())});
      }

      if (prop.getSet() != null) {
        String value = settings.getSetting(prop.getSet().substring(1));
        m.invoke(obj, new Object[] {basicTypeConvert(value, prop.getType())});
      }

      if (prop.getRef() != null) {
        Object refObj = getInstance(prop.getRef().substring(1), true);
        m.invoke(obj, new Object[] {refObj});
      }
    }

    return obj;
  }

  private Object makeInstanceByConstructorConfig(YmlBeanEntity bean)
      throws ClassNotFoundException, NoSuchMethodException, SecurityException,
          InstantiationException, IllegalAccessException, IllegalArgumentException,
          InvocationTargetException, RequiredBeanException, FailToNewInstanceException {
    YmlParamEntity[] params = bean.getParams();

    ArrayList<Class<?>> constructorParameterTypes = new ArrayList<Class<?>>();
    ArrayList<Object> constructorParameterObjs = new ArrayList<Object>();

    for (YmlParamEntity param : params) {
      constructorParameterTypes.add(Class.forName(realTypeMapping(param.getType())));

      if (param.getValue() != null) {
        constructorParameterObjs.add(basicTypeConvert(param.getValue(), param.getType()));
      }

      if (param.getSet() != null) {
        String value = settings.getSetting(param.getSet().substring(1));
        constructorParameterObjs.add(basicTypeConvert(value, param.getType()));
      }

      if (param.getRef() != null) {
        Object refObj = getInstance(param.getRef().substring(1), true);
        constructorParameterObjs.add(refObj);
      }
    }

    Constructor<?> c =
        bean.getKlass()
            .getDeclaredConstructor(constructorParameterTypes.toArray(new Class<?>[] {}));
    Object obj = c.newInstance(constructorParameterObjs.toArray(new Object[] {}));

    return obj;
  }

  private Object makeInstanceByAnnotation(YmlBeanEntity bean)
      throws NoSuchMethodException, SecurityException, InstantiationException,
          IllegalAccessException, IllegalArgumentException, InvocationTargetException,
          RequiredBeanException, ClassNotFoundException, FailToNewInstanceException {
    Constructor<?>[] constructArray = bean.getKlass().getDeclaredConstructors();
    Constructor<?> lazyInjectConstructor = null;

    for (Constructor<?> c : constructArray) {
      LazyInject lazyInject = c.getAnnotation(LazyInject.class);

      if (Objects.nonNull(lazyInject)) {
        lazyInjectConstructor = c;
        break;
      }
    }

    if (lazyInjectConstructor != null) {
      return makeInstanceByConstructor(lazyInjectConstructor);
    } else {
      return makeInstanceBySetter(bean.getKlass());
    }
  }

  private Object makeInstanceBySetter(Class<?> klass)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException,
          InvocationTargetException, NoSuchMethodException, SecurityException,
          RequiredBeanException, ClassNotFoundException, FailToNewInstanceException {
    Object obj = klass.getDeclaredConstructor().newInstance();

    Field[] fields = klass.getDeclaredFields();

    for (Field field : fields) {
      LazyInject a = field.getAnnotation(LazyInject.class);

      if (a != null) {
        Object setObj = null;

        if ("".equals(a.value())) {
          setObj = getInstance(field.getType(), null, a.require());
        } else {
          setObj = getInstance(field.getType(), Class.forName(a.value()), a.require());
        }

        String setMethod =
            "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        Method m = klass.getMethod(setMethod, new Class<?>[] {field.getType()});

        m.invoke(obj, new Object[] {setObj});
      }

      lazyConfigInject b = field.getAnnotation(lazyConfigInject.class);

      if (b != null) {
        String setMethod =
            "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        Method m = klass.getMethod(setMethod, new Class<?>[] {field.getType()});

        String value = settings.getSetting(b.value());
        m.invoke(obj, new Object[] {basicTypeConvert(value, field.getType().getName())});
      }
    }

    return obj;
  }

  private Object makeInstanceByConstructor(Constructor<?> constructor)
      throws RequiredBeanException, InstantiationException, IllegalAccessException,
          IllegalArgumentException, InvocationTargetException, FailToNewInstanceException {
    LazyInject lazyInject = constructor.getAnnotation(LazyInject.class);

    ArrayList<Object> constructorParameterObjs = new ArrayList<Object>();

    for (Class<?> parameterType : constructor.getParameterTypes()) {
      Object paramObj = getInstance(parameterType, null, lazyInject.require());
      constructorParameterObjs.add(paramObj);
    }

    Object obj = constructor.newInstance(constructorParameterObjs.toArray(new Object[] {}));

    return obj;
  }

  private String realTypeMapping(String type) {
    if (basicTypesMapping.containsKey(type)) {
      type = basicTypesMapping.get(type);
    }

    return type;
  }

  private Object basicTypeConvert(String value, String type) {
    Object obj = null;

    type = realTypeMapping(type);

    switch (type) {
      case "java.lang.Integer":
        obj = Integer.parseInt(value);
        break;
      case "java.lang.Float":
        obj = Float.parseFloat(value);
        break;
      case "java.lang.Dobule":
        obj = Double.parseDouble(value);
        break;
      case "java.lang.Long":
        obj = Long.parseLong(value);
        break;
      case "java.lang.Short":
        obj = Short.parseShort(value);
        break;
      case "java.lang.Character":
        obj = String.valueOf(value);
        break;
      case "java.lang.Boolean":
        obj = Boolean.valueOf(value);
        break;
      default:
        obj = value;
        break;
    }

    return obj;
  }
}
