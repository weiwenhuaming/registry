package com.github.fengdai.registry.compiler;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.lang.model.element.TypeElement;

public class ToManyBinding extends Binding {
  private final TypeElement mapperType;

  private final Map<Object, ItemViewClass> itemViewClassMap = new LinkedHashMap<>();

  ToManyBinding(TypeElement modelType, TypeElement mapperType) {
    super(modelType);
    this.mapperType = mapperType;
  }

  void add(Object binderElement, ItemViewClass itemViewClass) {
    itemViewClassMap.put(binderElement, itemViewClass);
  }

  public Map<Object, ItemViewClass> getItemViewClassMap() {
    return itemViewClassMap;
  }

  TypeElement getMapperType() {
    return mapperType;
  }
}
