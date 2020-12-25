/* (C)2020 */
package pers.edison.lazyapp.entity;

public class YmlBeanEntity {

  private String id;
  private String className;
  private YmlPropertyEntity[] properties;
  private YmlParamEntity[] params;

  private Class<?> klass;
  private boolean manually;

  public YmlBeanEntity() {
    this.manually = false;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public YmlPropertyEntity[] getProperties() {
    return properties;
  }

  public void setProperties(YmlPropertyEntity[] properties) {
    this.properties = properties;
  }

  public YmlParamEntity[] getParams() {
    return params;
  }

  public void setParams(YmlParamEntity[] params) {
    this.params = params;
  }

  public Class<?> getKlass() {
    return klass;
  }

  public void setKlass(Class<?> klass) {
    this.klass = klass;
  }

  public boolean isManually() {
    return manually;
  }

  public void setManually(boolean manually) {
    this.manually = manually;
  }
}
