package com.github.fengdai.registry.internal;

import com.github.fengdai.registry.Mapper;
import com.github.fengdai.registry.Registry;
import java.util.Map;

class ModelToMany<T, K> extends Model<T> {
  private final Class<? extends Mapper<T, K>> mapperClass;
  private Mapper<T, K> mapper = null;
  private final Map<K, Registry.ItemView<T, ?>> itemMap;

  ModelToMany(Class<T> modelClass, Class<? extends Mapper<T, K>> mapperClass,
      Map<K, Registry.ItemView<T, ?>> itemMap) {
    super(modelClass);
    this.mapperClass = mapperClass;
    this.itemMap = itemMap;
  }

  @Override Registry.ItemView<T, ?> getItemView(T item) {
    if (mapper == null) {
      mapper = Utils.newInstanceOf(mapperClass);
    }
    return itemMap.get(mapper.map(item));
  }
}
