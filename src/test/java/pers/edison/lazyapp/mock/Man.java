/* (C)2020 */
package pers.edison.lazyapp.mock;

import pers.edison.lazyapp.annotation.LazyBean;
import pers.edison.lazyapp.annotation.LazyInject;

@LazyBean
public class Man implements People {

  private String name;
  private Integer age;

  @LazyInject private People child;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public People getChild() {
    return child;
  }

  public void setChild(People child) {
    this.child = child;
  }
}
