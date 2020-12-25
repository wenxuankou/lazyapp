/* (C)2020 */
package pers.edison.lazyapp.entity;

public class YmlSettingEntity {

  private String name;
  private String file;
  private String value;

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
