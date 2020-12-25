/* (C)2020 */
package pers.edison.lazyapp.mock;

import pers.edison.lazyapp.annotation.LazyBean;

@LazyBean
public class EnglishBook implements Book {

  private String name;
  private Integer pages;

  public EnglishBook() {
    this.name = "my english book";
    this.pages = 200;
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
