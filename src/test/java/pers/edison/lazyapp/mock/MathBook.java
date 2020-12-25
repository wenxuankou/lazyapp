/* (C)2020 */
package pers.edison.lazyapp.mock;

import pers.edison.lazyapp.annotation.LazyBean;

@LazyBean
public class MathBook implements Book {

  private String name;
  private Integer pages;

  public MathBook() {
    this.name = "my math book";
    this.pages = 341;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPages() {
    return pages;
  }

  public void setPages(Integer pages) {
    this.pages = pages;
  }
}
